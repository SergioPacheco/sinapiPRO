package com.sinapipro.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SinapiPRO Showcase API")
                        .version("1.0.0")
                        .description("""
                                API REST para gestão de obras e orçamentos da construção civil, baseada na tabela SINAPI.
                                
                                ## Módulos
                                - **Budgets** — Orçamentos com etapas, itens, BDI e Curva ABC
                                - **SINAPI** — Composições, materiais e preços por estado/mês
                                - **Job Costing** — Códigos de custo, variância e WIP Report
                                - **Schedule** — Cronograma, Curva S e Caminho Crítico (CPM)
                                - **Measurements** — Medições com workflow de aprovação
                                - **Contracts** — Contratos e aditivos (change orders)
                                - **Procurement** — Cotação → Pedido → Recebimento
                                - **Equipment** — Gestão de equipamentos e alertas de manutenção
                                - **Daily Log** — Diário de obra
                                - **Analytics** — EVM, Fluxo de Caixa, Portfolio
                                
                                ## Autenticação
                                Use `POST /api/v1/auth/token` com grant_type=password para obter um JWT.
                                """)
                        .contact(new Contact()
                                .name("SinapiPRO")
                                .email("admin@sinapipro.dev")
                                .url("https://github.com/SergioPacheco/sinapiPRO"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .schemaRequirement("bearer-jwt", new SecurityScheme()
                        .name("bearer-jwt")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token obtained from POST /api/v1/auth/token"))
                .tags(List.of(
                        new Tag().name("Auth").description("Authentication and token management"),
                        new Tag().name("Budgets").description("Construction budget CRUD with filters and pagination"),
                        new Tag().name("Budget Detail").description("Budget stages, items, BDI, ABC curve and price adjustment"),
                        new Tag().name("Compositions").description("SINAPI composition catalog with cost calculation"),
                        new Tag().name("Materials").description("SINAPI materials and prices by state/month"),
                        new Tag().name("Job Costing").description("Cost codes, transactions, variance analysis and WIP report"),
                        new Tag().name("Schedule").description("Project schedule, S-Curve and Critical Path (CPM)"),
                        new Tag().name("Measurements").description("Periodic work measurements with approval workflow"),
                        new Tag().name("Contracts").description("Contracts with change orders (aditivos)"),
                        new Tag().name("Procurement").description("Purchase requests → quotations → orders → receiving"),
                        new Tag().name("Equipment").description("Equipment management, usage tracking and maintenance alerts"),
                        new Tag().name("Daily Log").description("Daily construction log (diário de obra)"),
                        new Tag().name("Analytics").description("Dashboard, EVM, cash flow and portfolio analytics"),
                        new Tag().name("Suppliers").description("Supplier management"),
                        new Tag().name("Invoices").description("Invoice management")));
    }
}
