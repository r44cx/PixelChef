# Pixel Chef
Team:
- **bergecyr:** Berger Cyrill
- **zimmenoe:** Zimmermann Noe

A single-player cooking game where players progress through multiple levels by selecting the correct ingredients from a given set to complete displayed meals. The game challenges players culinary knowledge, with each level increasing in difficulty.

Gameplay Mechanics:
- **Ingredient Selection:** In each round, players are presented with a variety of ingredients. They must choose the correct ones to add to the current dish based on the given name or image. A visual representation of a dish is shown, indicating the ingredients needed. Players interpret the dish and select the correct ingredients from the available options.
- **Levels and Progression:** The game consists of multiple levels, each increasing in difficulty by introducing more complex recipes and  a larger variety of ingredients.
- **Unlockable Content:** Successfully completing a levels unlocks a new recipe, which the player can view in their recipe book in the main menu.

Technologies:
- **Android SDK**
- **Kotlin/Java**

Technical feasability:

Android SDK and Kotlin/Java are suitable for this project. These tools are well-supported and contain the required UI/UX elements, animations, and interactions. Android’s View components will help to create clickable ingredients or make selections,... (supporting touch interaction). Levels can have predefined difficulty parameters that determine recipe complexity and the number of ingredients. Using a database or local storage (e.g., SQLite or JSON), we can save and manage levels, unlocked recipes, and player progress. Unlock logic (for the levels) can be managed with a simple flag or state variable linked to the level completion status.

User interaction:

Since the game requires ingredient selection, simple touch interactions can be used. The game should be responsive and offer feedback to confirm correct or incorrect choices. (For a better experience, the game could include visual indicators (like color changes or animations) when ingredients are selected correctly or incorrectly).The game’s increasing difficulty through more complex recipes and ingredients variety should be well-paced to avoid frustration.

Effort estimation:

The project should be completed over approximately 12 weeks as part of the MOBA1 course. We estimate the development time to be 12 weeks * 4 hours/week * 2 team members, with the exact time required depending on the project’s level of complexity we are going to implement.
