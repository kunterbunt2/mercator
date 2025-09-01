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
material = lib.create_material(m_name="m.container",m_color=(1.0, 1.0, 1.0, 1.0),m_metallic=0.5,m_roughness=0.5)
container.data.materials.append(material)
lib.create_bevel_modifier( m_root = container, m_name="b1", m_segments=1, m_width=.02 )


