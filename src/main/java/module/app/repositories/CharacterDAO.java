package module.app.repositories;

import module.app.models.Character;

import java.util.ArrayList;
import java.util.List;

public class CharacterDAO {

    private List<Character> charactersList = new ArrayList<>();

    {
        charactersList.add(new Character(1, "Magicien 1", "magicien", 100));
        charactersList.add(new Character(2, "Guerrier 1", "guerrier", 150));
        charactersList.add(new Character(3, "Magicien 2", "magicien", 80));
        charactersList.add(new Character(4, "Guerrier 2", "guerrier", 120));
    }

    public List<Character> findAll(){
        return this.charactersList;
    }

    public Character findById (int id){

        Character character = this.charactersList.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);

        return character;
    }

    public void save(Character character){
        this.charactersList.add(character);
    }

    public void delete(int id){
        this.charactersList.removeIf(character -> character.getId() == id);
    }
}

