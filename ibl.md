1. Environment Map

What it is:
A high dynamic range (HDR) texture (cube map or equirectangular) that represents the full surrounding environment. Think
of it as a panoramic image wrapped around your scene.

Usage:

Used mainly for reflections and background rendering (what you see if no geometry is there).

Provides the source for deriving irradiance and radiance maps.

Effect on scene:
Without processing, it looks "perfectly sharp," but unrealistic for rough surfaces because it only gives mirror-like
reflections.

2. Irradiance Map

What it is:
A blurred version of the environment map that contains only low-frequency lighting information.

Usage:

Simulates diffuse lighting: how rough/matte surfaces scatter light from the environment.

Achieved by convolving the environment map with a diffuse BRDF (essentially averaging).

Effect on scene:

Gives soft ambient light on objects.

Ensures diffuse materials (like wood, cloth, concrete) are realistically affected by the environment.

3. Radiance Map (Prefiltered Environment Map)

What it is:
A filtered version of the environment map, precomputed at different roughness levels.

Usage:

Simulates specular reflections across varying surface roughness.

For smooth surfaces → uses sharp reflections from the environment.

For rough surfaces → samples blurrier versions (mipmaps or roughness-based prefilter).

Effect on scene:

Metallic/reflective surfaces look realistic.

Rough metals reflect soft blurred highlights, while polished chrome mirrors the environment sharply.