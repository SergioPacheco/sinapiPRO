package com.sinapipro.api.config.settings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Tag(name = "Settings", description = "Global application settings")
@RestController
@RequestMapping("/api/v1/settings")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
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

    private String getValue(String key, String defaultValue) {
        return repository.findById(key).map(AppSettings::getValue).orElse(defaultValue);
    }

    private void save(String key, String value) {
        repository.findById(key).ifPresentOrElse(
                s -> s.setValue(value),
                () -> repository.save(new AppSettings(key, value))
        );
    }

    public record GlobalSettings(@NotBlank String state, @NotBlank String referenceMonth, boolean desonerated) {}
}
