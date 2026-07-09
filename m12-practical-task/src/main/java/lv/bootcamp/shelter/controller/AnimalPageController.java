package lv.bootcamp.shelter.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lv.bootcamp.shelter.form.AnimalForm;
import lv.bootcamp.shelter.model.AnimalType;
import lv.bootcamp.shelter.service.AnimalService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AnimalPageController {

    private final AnimalService animalService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/animals")
    public String animals(@RequestParam(required = false) AnimalType type, Model model) {
        model.addAttribute("animals", animalService.findAll(type));
        model.addAttribute("selectedType", type);
        return "animals";
    }

    @GetMapping("/animals/{id}")
    public String animal(@PathVariable Long id, Model model) {
        model.addAttribute("animal", animalService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal not found")));
        return "animal";
    }

    @GetMapping("/animals/new")
    public String newAnimalForm(Model model) {
        model.addAttribute("form", new AnimalForm(null, null, null, null, null, null));
        return "animals-new";
    }

    @PostMapping("/animals")
    public String createAnimal(@Valid @ModelAttribute("form") AnimalForm form,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "animals-new";
        }

        animalService.create(form);
        redirectAttributes.addFlashAttribute("message", "Animal added!");
        return "redirect:/animals";
    }

    @PostMapping("/animals/{id}/adopt")
    public String adoptAnimal(@PathVariable Long id,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        animalService.adopt(id, authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal not found"));
        redirectAttributes.addFlashAttribute("message", "Animal adopted!");
        return "redirect:/animals";
    }
}
