package module.app.controllers;
import module.app.models.Character;
import module.app.models.CharacterForm;
import module.app.repositories.CharacterDAO;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SpringBootApplication
@Controller
public class CharacterController {

    CharacterDAO characterDAO = new CharacterDAO();

    private static List<Integer> idList = new ArrayList<>();

    @RequestMapping("/characters")
    public String getCharacters(Model model) {

        RestTemplate restTemplate = new RestTemplate();
        List<Object> charactersList = Arrays.asList(Objects.requireNonNull(restTemplate.getForObject("http://localhost:8080/characters", Object[].class)));
        model.addAttribute("charactersList", charactersList);

        return "characters";
    }

    @RequestMapping(value = {"/addCharacter"}, method = RequestMethod.GET)
    public String showAddCharacter(Model model) {

        CharacterForm characterForm = new CharacterForm();

        model.addAttribute("characterForm", characterForm);

        return "addCharacter";
    }

    @RequestMapping("/addCharacter")
    public String addCharacter(@ModelAttribute("characterForm") CharacterForm characterForm) {

        RestTemplate restTemplate = new RestTemplate();

        String name = characterForm.getName();
        String type = characterForm.getType();
        int id = idList.size() > 0 ? idList.get(0) : characterDAO.findAll().size()+1;

        if(name != null && name.length()>0 //
                && type != null && type.length()>0) {
            Character newCharacter = new Character(id,name,type,150);
            characterDAO.save(newCharacter);
            if (idList.size() > 0){
                idList.remove(0);
            }
            restTemplate.postForObject("http://localhost:8080/characters", newCharacter, Object.class);

            return "redirect:/characters";

        }

        return "addCharacter";
    }

    @RequestMapping(value = {"/updateCharacter/{id}"}, method = RequestMethod.GET)
    public String updateCharacter(Model model, @PathVariable("id") int id) {

        for (Character character : characterDAO.findAll()) {
            if (character.getId() == id) {
                CharacterForm newCharacterForm = new CharacterForm();
                newCharacterForm.setId(character.getId());
                newCharacterForm.setName(character.getName());
                newCharacterForm.setType(character.getType());
                model.addAttribute("characterForm", newCharacterForm);
                break;
            }
        }
        return "updateCharacter";
    }


    @RequestMapping("/updateCharacter")
    public String updateCharacter(@ModelAttribute("characterForm") CharacterForm characterForm) {

        RestTemplate restTemplate = new RestTemplate();

        int id = characterForm.getId();
        String name = characterForm.getName();
        String type = characterForm.getType();

        for (Character character : characterDAO.findAll()) {
            if (character.getId() == id) {
                character.setName(name);
                character.setType(type);
                restTemplate.put(String.format("http://localhost:8080/characters/%s",id), character, Object.class);
                return "redirect:/characters";
            }
        }

        return "redirect:/characters";
    }

    @RequestMapping(value = {"/deleteCharacter/{id}"}, method = RequestMethod.GET)
    public String deleteCharacter(@PathVariable("id") int id) {

        RestTemplate restTemplate = new RestTemplate();
        characterDAO.delete(id);
        idList.add(id);

        restTemplate.delete(String.format("http://localhost:8080/characters/%s",id), Object.class);

        return "redirect:/characters";
    }


}

