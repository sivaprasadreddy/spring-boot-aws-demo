package com.sivalabs.awsdemo;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchTest {

	JavaClasses importedClasses = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("com.sivalabs.awsdemo");

	@Test
	void servicesAndRepositoriesShouldNotDependOnWebLayer() {
		noClasses().that()
            .resideInAnyPackage("com.sivalabs.awsdemo.domain.service..").or()
            .resideInAnyPackage("com.sivalabs.awsdemo.domain.repository..")
            .should().dependOnClassesThat()
				.resideInAnyPackage("com.sivalabs.awsdemo.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
	}

	@Test
	void shouldNotUseFieldInjection() {
		noFields().should().beAnnotatedWith(Autowired.class).check(importedClasses);
	}

	@Test
	void shouldFollowLayeredArchitecture() {
		layeredArchitecture()
            .layer("Config").definedBy("..config..")
            .layer("Web").definedBy("..web..")
            .layer("Service").definedBy("..service..")
            .layer("Persistence").definedBy("..repository..")

            .whereLayer("Web").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Config", "Web")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service")
            .check(importedClasses);
	}

	@Test
	void shouldFollowNamingConvention() {
		classes().that()
            .resideInAPackage("com.sivalabs.awsdemo.repository")
            .should().haveSimpleNameEndingWith("Repository")
            .check(importedClasses);

		classes().that()
            .resideInAPackage("com.sivalabs.awsdemo.service")
            .should().haveSimpleNameEndingWith("Service")
            .check(importedClasses);
	}

	@Test
	void shouldNotUseJunit4Classes() {
		JavaClasses classes = new ClassFileImporter().importPackages("com.sivalabs.awsdemo");

		noClasses()
            .should().accessClassesThat().resideInAnyPackage("org.junit")
				.because("Tests should use Junit5 instead of Junit4")
            .check(classes);

		noMethods()
            .should().beAnnotatedWith("org.junit.Test")
            .orShould().beAnnotatedWith("org.junit.Ignore")
				.because("Tests should use Junit5 instead of Junit4")
            .check(classes);
	}

}
