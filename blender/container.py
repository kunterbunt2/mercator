import bpy

# -------------------------------------------------------
import os
import sys
import importlib
# Path where the blend file is saved
blend_dir = os.path.dirname(bpy.data.filepath)
if blend_dir not in sys.path:
    sys.path.append(blend_dir)
import lib
importlib.reload(lib)
# -------------------------------------------------------


# main script
lib.clear_scene()



bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(0, 0, 0), scale=(1, 1, 1))
container = bpy.context.active_object
container.name = 'container'
material = lib.create_material( name="m.container", color=(1.0, 1.0, 1.0, 1.0), metallic=0.5, roughness=0.5)
container.data.materials.append(material)
lib.create_bevel_modifier( root = container, name="b1", segments=2, width=.02 )
# enable smooth shading
bpy.ops.object.shade_smooth()
# enable Auto Smooth so edges stay sharp
mod = container.modifiers.new(name="Smooth by Angle", type='NODES')
bpy.ops.object.shade_auto_smooth(use_auto_smooth=True, angle=1.0472)


