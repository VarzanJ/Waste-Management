
package com.ecosmart.ecosmart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WasteController {

    private final WasteRecordRepository repository;
    private final SmartBinRepository smartBinRepository;

    public WasteController(
            WasteRecordRepository repository,
            SmartBinRepository smartBinRepository) {
        this.repository = repository;
        this.smartBinRepository = smartBinRepository;
    }

    // Home page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Register waste
    @PostMapping("/register-waste")
    public String registerWaste(
            @RequestParam String category,
            @RequestParam BigDecimal quantity,
            @RequestParam String location,
            Model model) {

        if (quantity.compareTo(BigDecimal.ZERO) <= 0
                || category.isBlank()
                || location.isBlank()) {

            model.addAttribute("error",
                    "Please enter a valid category, positive quantity and location.");

            return "index";
        }

        WasteRecord record = new WasteRecord(
                category.trim(),
                quantity,
                location.trim());

        WasteRecord savedRecord = repository.save(record);

        model.addAttribute("id", savedRecord.getId());
        model.addAttribute("category", savedRecord.getCategory());
        model.addAttribute("quantity", savedRecord.getQuantity());
        model.addAttribute("location", savedRecord.getLocation());

        return "success";
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        List<WasteRecord> records = repository.findAll();
        List<SmartBin> bins = smartBinRepository.findAll();

        long totalBins = bins.size();

        long fullBins = bins.stream()
                .filter(bin -> bin.getFillPercentage() > 70)
                .count();

        long halfFullBins = bins.stream()
                .filter(bin -> bin.getFillPercentage() > 30
                        && bin.getFillPercentage() <= 70)
                .count();

        long emptyBins = bins.stream()
                .filter(bin -> bin.getFillPercentage() <= 30)
                .count();

        model.addAttribute("totalBins", totalBins);
        model.addAttribute("fullBins", fullBins);
        model.addAttribute("halfFullBins", halfFullBins);
        model.addAttribute("emptyBins", emptyBins);

        BigDecimal totalQuantity = records.stream()
                .map(WasteRecord::getQuantity)
                .filter(quantity -> quantity != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Set<String> categories = records.stream()
                .map(WasteRecord::getCategory)
                .filter(category -> category != null)
                .collect(Collectors.toSet());

        Set<String> locations = records.stream()
                .map(WasteRecord::getLocation)
                .filter(location -> location != null)
                .collect(Collectors.toSet());

        List<WasteRecord> recentRecords = records.stream()
                .sorted(Comparator.comparing(
                        WasteRecord::getId,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();

        Map<String, BigDecimal> categoryTotals = records.stream()
                .filter(record -> record.getCategory() != null
                        && record.getQuantity() != null)
                .collect(Collectors.groupingBy(
                        WasteRecord::getCategory,
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                WasteRecord::getQuantity,
                                BigDecimal::add)));

        model.addAttribute("totalRecords", records.size());
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("categoryCount", categories.size());
        model.addAttribute("locationCount", locations.size());
        model.addAttribute("recentRecords", recentRecords);

        model.addAttribute(
                "chartLabels",
                new ArrayList<>(categoryTotals.keySet()));

        model.addAttribute(
                "chartValues",
                categoryTotals.values().stream()
                        .map(BigDecimal::doubleValue)
                        .toList());

        return "dashboard";
    }

    // View all waste records
    @GetMapping("/records")
    public String viewRecords(Model model) {

        List<WasteRecord> records = repository.findAll();

        records.sort(Comparator.comparing(
                WasteRecord::getId,
                Comparator.nullsLast(Comparator.reverseOrder())));

        model.addAttribute("records", records);

        return "records";
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String editWaste(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return repository.findById(id)
                .map(record -> {
                    model.addAttribute("record", record);
                    return "edit-waste";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "message", "Waste record was not found.");
                    return "redirect:/records";
                });
    }

    // Update waste record
    @PostMapping("/update/{id}")
    public String updateWaste(
            @PathVariable Long id,
            @RequestParam String category,
            @RequestParam BigDecimal quantity,
            @RequestParam String location,
            RedirectAttributes redirectAttributes) {

        if (category.isBlank()
                || location.isBlank()
                || quantity.compareTo(BigDecimal.ZERO) <= 0) {

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Enter a valid category, positive quantity and location.");

            return "redirect:/edit/" + id;
        }

        return repository.findById(id)
                .map(record -> {
                    record.setCategory(category.trim());
                    record.setQuantity(quantity);
                    record.setLocation(location.trim());

                    repository.save(record);

                    redirectAttributes.addFlashAttribute(
                            "message",
                            "Waste record updated successfully.");

                    return "redirect:/records";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "message", "Waste record was not found.");
                    return "redirect:/records";
                });
    }

    // Delete a waste record
    @PostMapping("/delete/{id}")
    public String deleteRecord(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (repository.existsById(id)) {
            repository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Waste record deleted successfully.");

        } else {
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Waste record was not found.");
        }

        return "redirect:/records";
    }
}
