package com.sinapipro.api.config.settings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
@Tag(name = "Settings", description = "Global application settings")
@RestController
@RequestMapping("/api/v1/settings")
@PreAuthorize("@perm.check('settings.read')")
public class SettingsController {

    private final AppSettingsRepository repository;

    public SettingsController(AppSettingsRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Get global settings (state, referenceMonth, desonerated)")
    @GetMapping
    public GlobalSettings get() {
        String state = getValue(AppSettings.DEFAULT_STATE, "SP");
        String month = getValue(AppSettings.DEFAULT_REFERENCE_MONTH, LocalDate.now().withDayOfMonth(1).toString());
        boolean desonerated = Boolean.parseBoolean(getValue(AppSettings.DEFAULT_DESONERATED, "false"));
        return new GlobalSettings(state, month, desonerated);
    }

    @Operation(summary = "Update global settings")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public GlobalSettings update(@Valid @RequestBody GlobalSettings settings) {
        save(AppSettings.DEFAULT_STATE, settings.state());
        save(AppSettings.DEFAULT_REFERENCE_MONTH, settings.referenceMonth());
        save(AppSettings.DEFAULT_DESONERATED, String.valueOf(settings.desonerated()));
        return settings;
    }

    @Operation(summary = "Get company settings")
    @GetMapping("/company")
    public CompanySettings getCompany() {
        return new CompanySettings(
                getValue("COMPANY_NAME", ""),
                getValue("COMPANY_CNPJ", ""),
                getValue("COMPANY_STATE_REGISTRATION", ""),
                getValue("COMPANY_ADDRESS", ""),
                getValue("COMPANY_CITY", ""),
                getValue("COMPANY_STATE", ""),
                getValue("COMPANY_ZIP_CODE", ""),
                getValue("COMPANY_PHONE", ""),
                getValue("COMPANY_EMAIL", ""),
                getValue("COMPANY_WEBSITE", "")
        );
    }

    @Operation(summary = "Update company settings")
    @PutMapping("/company")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CompanySettings updateCompany(@RequestBody CompanySettings settings) {
        save("COMPANY_NAME", settings.name());
        save("COMPANY_CNPJ", settings.cnpj());
        save("COMPANY_STATE_REGISTRATION", settings.stateRegistration());
        save("COMPANY_ADDRESS", settings.address());
        save("COMPANY_CITY", settings.city());
        save("COMPANY_STATE", settings.state());
        save("COMPANY_ZIP_CODE", settings.zipCode());
        save("COMPANY_PHONE", settings.phone());
        save("COMPANY_EMAIL", settings.email());
        save("COMPANY_WEBSITE", settings.website());
        return settings;
    }

    private String getValue(String key, String defaultValue) {
        return repository.findById(key).map(AppSettings::getValue).orElse(defaultValue);
    }

    private void save(String key, String value) {
        repository.findById(key).ifPresentOrElse(
                s -> s.setValue(value != null ? value : ""),
                () -> repository.save(new AppSettings(key, value != null ? value : ""))
        );
    }

    public record GlobalSettings(@NotBlank String state, @NotBlank String referenceMonth, boolean desonerated) {}
    public record CompanySettings(String name, String cnpj, String stateRegistration, String address,
                                  String city, String state, String zipCode, String phone,
                                  String email, String website) {}
}
