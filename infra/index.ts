import * as pulumi from "@pulumi/pulumi";
import * as aws from "@pulumi/aws";
import * as awsx from "@pulumi/awsx";

const config = new pulumi.Config();
const environment = config.require("environment");
const appName = "todolist-pulumi";
const withAppEnvPrefix = (name : string) => appName + "-" + environment + "-" + name;

const dbName = config.require("db_name");
const dbUsername = config.require("db_username");
const dbPassword = config.requireSecret("db_password");

const vpcName = withAppEnvPrefix("vpc");
const clusterName = withAppEnvPrefix("cluster");
const queue_name = withAppEnvPrefix("queue");
const dockerImage = "sivaprasadreddy/spring-boot-aws-pulumi-demo";

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

const db = new aws.rds.Instance(withAppEnvPrefix("db"), {
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
    Name: withAppEnvPrefix("db"),
    Environment: environment
  },
});

const connectionString = pulumi.interpolate `jdbc:postgresql://${db.endpoint}/${dbName}?sslmode=disable`;

const alb = new awsx.lb.ApplicationLoadBalancer(withAppEnvPrefix("alb"), {
  vpc,
  external: true,
  securityGroups: cluster.securityGroups,
  tags: {
    Name: withAppEnvPrefix("alb"),
    Environment: environment
  },
});

const tg = alb.createTargetGroup(withAppEnvPrefix("tg"), {
  vpc,
  port: 8080,
  healthCheck: {
    path: "/actuator/health",
    timeout: 60,
    interval: 120,
  },
  tags: {
    Name: withAppEnvPrefix("tg"),
    Environment: environment
  },
});

const listener = tg.createListener(withAppEnvPrefix("listener"), {
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
      Resource: queue.arn,
      Action: "sqs:*",
    }],
  },
});

const logGroup = new aws.cloudwatch.LogGroup("spring-boot-aws-pulumi-demo-logs", {
  retentionInDays: 7,
});

const service = new awsx.ecs.FargateService(withAppEnvPrefix("service"), {
  cluster,
  desiredCount: 1,
  taskDefinitionArgs: {
    taskRole: allowSqsRole,
    logGroup: logGroup,
    containers: {
      service: {
        image: dockerImage,
        portMappings: [listener],
        memory: 1024,
        cpu: 256,
        environment: [
          {name: "SERVER_PORT", value: "8080"},
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
