import os
from openai import OpenAI
import requests

# Set up OpenAI client
client = OpenAI(api_key="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")

# Function to determine type and generate the corresponding prompt
def generate_prompt(filename):
    if filename.startswith("meals_"):
        name = filename[len("meals_"):].replace("_", " ").replace(".jpg", "")
        return f"A highly detailed pixel art illustration of a fully prepared {name}, designed for a cooking-themed game. The artwork should feature a retro gaming style with vibrant, appealing colors and intricate details that make the dish look delicious and inviting. The {name} should be presented in an artistic and visually engaging manner, showcasing its key ingredients. Use a clean, solid white background to emphasize the dish and ensure it can be easily integrated into the game's interface. The overall aesthetic should feel playful, immersive, and fitting for a food-centric pixel art game."
    elif filename.startswith("ingredients_"):
        name = filename[len("ingredients_"):].replace("_", " ").replace(".jpg", "")
        return f"A highly detailed pixel art depiction of {name}, suitable as an ingredient in a cooking-themed game. The artwork should have a retro gaming style with vibrant and appealing colors. The {name} should be stylized but easily recognizable, arranged in a visually engaging manner. Use a simple, solid white background to make it easy for removal or integration into the game's interface. Ensure the design feels playful and inviting, matching the aesthetic of a food-centric pixel art game. Make sure it's only the raw ingredient, not a combination of multiple things. Also, we want only one object and a solid background, not additional text or objects."
    return None

# Function to create or modify images using DALL-E
def process_images(directory):
    for filename in os.listdir(directory):
        if filename.endswith(".jpg"):
            prompt = generate_prompt(filename)
            if not prompt:
                continue
            
            # DALL-E generation call using new API
            response = client.images.generate(
                model="dall-e-2",
                prompt=prompt,
                n=1,
                size="1024x1024"
            )
            image_url = response.data[0].url

            # Save the image with the same filename
            output_path = os.path.join(directory, filename)
            with open(output_path, "wb") as file:
                file.write(requests.get(image_url).content)

            print(f"Processed and saved: {filename}")

# Main execution
if __name__ == "__main__":
    current_directory = os.getcwd()
    process_images(current_directory)
