import * as pulumi from "@pulumi/pulumi";
import * as aws from "@pulumi/aws";
import * as awsx from "@pulumi/awsx";

const config = new pulumi.Config();
const environment = config.require("environment")
const envPrefix = environment + "-"
const withEnvPrefix = (name : string) => envPrefix + name;
const withEnvAppPrefix = (name : string) => "todolist-pulumi-" + withEnvPrefix(name);

const vpcName = withEnvAppPrefix("vpc");
const clusterName = withEnvAppPrefix("cluster");

const dbName = config.require("db_name");
const dbUsername = config.require("db_username");
const dbPassword = config.requireSecret("db_password");

const queue_name = withEnvAppPrefix("queue");

const vpc = new awsx.ec2.Vpc(vpcName, {
  numberOfAvailabilityZones: 2,
  subnets: [
    { type: "public" },
    { type: "isolated" },
  ],
  tags: {
    Name: vpcName,
    Environment: environment
  }
});

const queue = new aws.sqs.Queue(queue_name, {
  name: queue_name,
  delaySeconds: 0,
  maxMessageSize: 262144,
  messageRetentionSeconds: 345600,
  receiveWaitTimeSeconds: 10,
});

const cluster = new awsx.ecs.Cluster(clusterName, {
  vpc,
  tags: {
    Name: clusterName,
    Environment: environment
  },
});

const subnetGroup = new aws.rds.SubnetGroup("dbsubnets", {
  subnetIds: vpc.publicSubnetIds,
});

const db = new aws.rds.Instance(withEnvAppPrefix("db"), {
  engine: "postgres",
  instanceClass: aws.rds.InstanceTypes.T2_Micro,
  allocatedStorage: 5,
  dbSubnetGroupName: subnetGroup.id,
  vpcSecurityGroupIds: cluster.securityGroups.map(g => g.id),
  name: dbName,
  username: dbUsername,
  password: dbPassword,
  skipFinalSnapshot: true,
  publiclyAccessible: environment !== "prod",
  tags: {
    Name: withEnvAppPrefix("db"),
    Environment: environment
  },
});

const connectionString = pulumi.interpolate `jdbc:postgresql://${db.endpoint}/${dbName}?sslmode=disable`;

const alb = new awsx.lb.ApplicationLoadBalancer(withEnvAppPrefix("alb"), {
  vpc,
  external: true,
  securityGroups: cluster.securityGroups,
  tags: {
    Name: withEnvAppPrefix("alb"),
    Environment: environment
  },
});

const tg = alb.createTargetGroup(withEnvAppPrefix("tg"), {
  vpc,
  port: 80,
  healthCheck: {
    path: "/actuator/health",
    timeout: 60,
    interval: 120,
  },
  tags: {
    Name: withEnvAppPrefix("tg"),
    Environment: environment
  },
});

const listener = tg.createListener(withEnvAppPrefix("listener"), {
  vpc,
  port: 80,
  external: true,
});

const allowSqsRole = new aws.iam.Role("allow-sqs-role", {
  description: "Allow access to SQS",
  assumeRolePolicy: aws.iam.assumeRolePolicyForPrincipal({ Service: "ecs-tasks.amazonaws.com" }),
});

const policy = new aws.iam.RolePolicy("allow-sqs-policy", {
  role: allowSqsRole,
  policy: {
    Version: "2012-10-17",
    Statement: [{
      Sid: "AllowSQSAccess",
      Effect: "Allow",
      Resource: "*",
      Action: "sqs:*",
    }],
  },
});

const service = new awsx.ecs.FargateService(withEnvAppPrefix("service"), {
  cluster,
  desiredCount: 1,
  taskDefinitionArgs: {
    taskRole: allowSqsRole,
    containers: {
      service: {
        image: awsx.ecs.Image.fromDockerBuild("spring-boot-aws-pulumi-demo",{
          context: "../",
          dockerfile: "../Dockerfile",
        }),
        portMappings: [listener],
        memory: 1024,
        cpu: 256,
        environment: [
          {name: "SERVER_PORT", value: "80"},
          {name: "SPRING_PROFILES_ACTIVE", value: "aws"},
          {name: "MYAPP_QUEUENAME", value: queue_name},
          {name: "SPRING_DATASOURCE_DRIVER_CLASS_NAME", value: "org.postgresql.Driver"},
          {name: "SPRING_DATASOURCE_URL", value: connectionString},
          {name: "SPRING_DATASOURCE_USERNAME", value: dbUsername},
          {name: "SPRING_DATASOURCE_PASSWORD", value: dbPassword},
        ]
      },
    },
  },
});

export const db_url = connectionString
export const queue_url = queue.id;
export const app_url = pulumi.interpolate`http://${listener.endpoint.hostname}:${listener.endpoint.port}`;
