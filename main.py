from PIL import Image
import math
import os
import time

class PaletteConverter:

    PALETTE_COLORS = []

    def __init__(self, palette_path:str) -> None:
        self.load_palette(palette_path=palette_path)

    def load_palette (self, palette_path:str) -> None:
        palette = Image.open(palette_path).convert("RGB")
        width, height = palette.size
        pixels = palette.load()
        for y in range(height):
            for x in range(width):
                rgb = pixels[x, y]
                self.PALETTE_COLORS.append(rgb)

    def convert_image_colors_to_palette_and_save (self, file_path:str) -> None:
        print(f"input file: {file_path}")
        start = time.time()
        image = Image.open(f"input/{file_path}").convert("RGB")
        width, height = image.size
        result_image = Image.new("RGB", (width, height))
        pixels = image.load()
        for y in range(height):
            for x in range(width):
                rgb = pixels[x, y]
                nearest_rgb = self.get_nearest_color(rgb)
                result_image.putpixel(xy = (x, y), value = nearest_rgb)
        result_image.save(f"output/{file_path}")
        finish = time.time()
        print(f"time to render: {((finish - start)*100):.2f} ms")

    def get_nearest_color (self, rgb:tuple) -> tuple:
        min_length = 999999
        nearest_color = (0, 0, 0)
        for color in self.PALETTE_COLORS:
            length = math.sqrt((color[0] - rgb[0])**2 + (color[1] - rgb[1])**2 + (color[2] - rgb[2])**2)
            if (length < min_length):
                min_length = length
                nearest_color = color
        return nearest_color

if __name__ == "__main__":
    converter = PaletteConverter("palette-1.png")
    output_files = os.listdir("output")
    inputs_files = os.listdir("input")
    for file_path in inputs_files:
        if (file_path not in output_files):
            converter.convert_image_colors_to_palette_and_save(file_path)