import bpy
import os
import random

# -------------------------
# 1. Generate Noisy Gradient Texture
# -------------------------
width, height = 256, 1024
image = bpy.data.images.new("ThrusterGradient", width=width, height=height, alpha=True)

pixels = []

for y in range(height):
    t = y / (height - 1)  # gradient factor 0..1

    # Base gradient colors (white → blue)
    r = (1.0 - t) * 1.0 + t * 0.2
    g = (1.0 - t) * 1.0 + t * 0.5
    b = (1.0 - t) * 1.0 + t * 1.0
    a = 1.0

    for x in range(width):
        # Add noise for randomness
        noise = (random.random() - 0.5) * 0.15 * (1.0 - t)  # stronger near engine
        rn = max(0.0, min(1.0, r + noise))
        gn = max(0.0, min(1.0, g + noise * 0.5))
        bn = max(0.0, min(1.0, b + noise * 1.5))

        # Slight horizontal flicker
        flicker = (random.random() - 0.5) * 0.1 * (1.0 - t)
        an = max(0.0, min(1.0, a + flicker))

        pixels.extend([rn, gn, bn, an])

# Assign pixel data
image.pixels = pixels

# Save image to disk
tex_path = os.path.join(bpy.path.abspath("//"), "thruster_gradient_noisy.png")
image.filepath_raw = tex_path
image.file_format = 'PNG'
image.save()
print(f"Texture saved at {tex_path}")