package com.sinapipro.api;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Architecture fitness functions — enforces module boundaries automatically.
 *
 * Rules:
 * 1. Domain layer MUST NOT depend on API layer (inward dependency only)
 * 2. Domain layer MUST NOT depend on Application layer
 * 3. No circular dependencies between modules
 * 4. Config package MUST NOT depend on domain logic
 */
@AnalyzeClasses(packages = "com.sinapipro.api", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureBoundaryTest {

    @ArchTest
    static final ArchRule domain_does_not_depend_on_api =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..api..")
                    .because("Domain layer must not know about REST controllers or DTOs");

    @ArchTest
    static final ArchRule domain_does_not_depend_on_application =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..application..")
                    .because("Domain layer must not depend on service/application layer");

    @ArchTest
    static final ArchRule domain_does_not_depend_on_config =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..config..")
                    .because("Domain layer must not depend on Spring configuration");

    @ArchTest
    static final ArchRule no_cycles_between_modules =
            slices().matching("com.sinapipro.api.(*)..")
                    .should().beFreeOfCycles()
                    .because("Modules must not have circular dependencies");
}
