
package com.ecosmart.ecosmart;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SmartBinController {

    private final SmartBinRepository repository;

    public SmartBinController(SmartBinRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/smart-bins")
    public String showSmartBins(Model model) {
        List<SmartBin> bins = repository.findAll();
        model.addAttribute("bins", bins);
        return "smart-bins";
    }

    @PostMapping("/smart-bins/save")
    public String saveSmartBin(
            @RequestParam String binName,
            @RequestParam String location,
            @RequestParam int fillPercentage,
            RedirectAttributes redirectAttributes) {

        if (binName.isBlank()
                || location.isBlank()
                || fillPercentage < 0
                || fillPercentage > 100) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Enter valid details. Fill percentage must be 0-100.");

            return "redirect:/smart-bins";
        }

        String status;

        if (fillPercentage <= 30) {
            status = "Empty";
        } else if (fillPercentage <= 70) {
            status = "Half Full";
        } else {
            status = "Full";
        }

        SmartBin bin = new SmartBin(
                binName.trim(),
                location.trim(),
                fillPercentage,
                status);

        repository.save(bin);

        redirectAttributes.addFlashAttribute(
                "message",
                "Smart Bin saved successfully!");

        return "redirect:/smart-bins";
    }
}

