import bpy
import math
import os

def hex_to_rgba(hex_str):
    # Remove '#' if present
    hex_str = hex_str.lstrip('#')
    # Convert pairs to integers
    r = int(hex_str[0:2], 16) / 255.0
    g = int(hex_str[2:4], 16) / 255.0
    b = int(hex_str[4:6], 16) / 255.0
    a = int(hex_str[6:8], 16) / 255.0 if len(hex_str) == 8 else 1.0
    return (r, g, b, a)    

def srgb_to_linear(c):
    """Convert a single sRGB channel (0-1) to linear RGB"""
    if c <= 0.04045:
        return c / 12.92
    else:
        return ((c + 0.055) / 1.055) ** 2.4

def hex_to_linear_rgba(hex_str):
    hex_str = hex_str.lstrip("#")
    r = int(hex_str[0:2], 16) / 255.0
    g = int(hex_str[2:4], 16) / 255.0
    b = int(hex_str[4:6], 16) / 255.0
    a = int(hex_str[6:8], 16) / 255.0 if len(hex_str) == 8 else 1.0
    return (
        srgb_to_linear(r),
        srgb_to_linear(g),
        srgb_to_linear(b),
        a
    )

def create_material(m_name, m_color, m_metallic,m_roughness,m_alpha=1):
    mat = bpy.data.materials.get(m_name)
    if mat is None:
        mat = bpy.data.materials.new(name=m_name)
        #mat = bpy.ops.material.new()
        #bpy.context.object.active_material.name = "material.container.plane"
        mat.use_nodes = True
        principled_bsdf = bpy.data.materials[m_name].node_tree.nodes["Principled BSDF"].inputs[2]
        principled_bsdf = mat.node_tree.nodes["Principled BSDF"]
        principled_bsdf.inputs['Base Color'].default_value = m_color
        principled_bsdf.inputs['Metallic'].default_value = m_metallic
        principled_bsdf.inputs['Roughness'].default_value = m_roughness
        principled_bsdf.inputs['Alpha'].default_value = m_alpha
    return mat



def clear_scene():
    # Clear scene
    # Directly remove objects from bpy.data
    for obj in bpy.data.objects:
        bpy.data.objects.remove(obj, do_unlink=True)    
    #bpy.ops.object.select_all(action='SELECT')
    #bpy.ops.object.delete()
    # Remove all orphaned materials
    for mat in bpy.data.materials[:]:  # use a copy of the list
        if mat.users == 0:
            bpy.data.materials.remove(mat)
    bpy.ops.outliner.orphans_purge(do_local_ids=True, do_linked_ids=True, do_recursive=True)
    bpy.context.scene.render.engine = 'CYCLES'

    # Path to your HDRI file
    hdri_path = "//app_balkon_cloudy.hdr"
    # Ensure world exists and uses nodes
    world = bpy.data.worlds["World"]
    world.use_nodes = True
    nodes = world.node_tree.nodes
    links = world.node_tree.links

    # Clear existing nodes (optional, if you want a fresh world setup)
    nodes.clear()

    # Create nodes
    output = nodes.new(type="ShaderNodeOutputWorld")
    bg = nodes.new(type="ShaderNodeBackground")
    env_tex = nodes.new(type="ShaderNodeTexEnvironment")

    # Load the HDRI image
    env_tex.image = bpy.data.images.load(hdri_path)

    # Arrange nodes for readability (not necessary, just nice)
    output.location = (300, 0)
    bg.location = (0, 0)
    env_tex.location = (-300, 0)

    # Link them: Environment → Background → World Output
    links.new(env_tex.outputs["Color"], bg.inputs["Color"])
    links.new(bg.outputs["Background"], output.inputs["Surface"])
        


clear_scene()
# -------------------------
# 2. Create Cone Mesh
# -------------------------
bpy.ops.mesh.primitive_cone_add(
    vertices=32, 
    radius1=0.25,  # base (engine side)
    radius2=0.05,  # tip (flame end)
    depth=2.0, 
    location=(0, 0, 0),
    rotation=(0,math.radians(90),0)
)
flame = bpy.context.active_object
flame.name = "Thruster_Flame"

# -------------------------
# 3. Create Material with Principled BSDF
# -------------------------
tex_path = os.path.join(bpy.path.abspath("//"), "thruster_gradient.png")
mat = bpy.data.materials.new(name="Thruster_Material")
mat.use_nodes = True
nodes = mat.node_tree.nodes
links = mat.node_tree.links

bsdf = nodes.get("Principled BSDF")

# Add image texture node
tex_node = nodes.new("ShaderNodeTexImage")
tex_node.location = (-400, 0)
tex_node.image = bpy.data.images.load(tex_path)

# Link texture → Principled BSDF
links.new(tex_node.outputs["Color"], bsdf.inputs["Base Color"])
links.new(tex_node.outputs["Alpha"], bsdf.inputs["Alpha"])

# Assign material
if flame.data.materials:
    flame.data.materials[0] = mat
else:
    flame.data.materials.append(mat)

# Enable transparency
mat.blend_method = 'BLEND'
#mat.shadow_method = 'NONE'

print("Cone thruster flame created with gradient texture (no external libraries)!")
    

