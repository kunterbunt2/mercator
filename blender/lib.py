import bpy

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

def create_material( name, color, metallic, roughness, alpha=1):
    mat = bpy.data.materials.get(name)
    if mat is None:
        mat = bpy.data.materials.new(name=name)
        #mat = bpy.ops.material.new()
        #bpy.context.object.active_material.name = "material.container.plane"
        mat.use_nodes = True
        principled_bsdf = bpy.data.materials[name].node_tree.nodes["Principled BSDF"].inputs[2]
        principled_bsdf = mat.node_tree.nodes["Principled BSDF"]
        principled_bsdf.inputs['Base Color'].default_value = color
        principled_bsdf.inputs['Metallic'].default_value = metallic
        principled_bsdf.inputs['Roughness'].default_value = roughness
        principled_bsdf.inputs['Alpha'].default_value = alpha
    return mat

def create_boolean_modifier( root, name, operation, object, apply=False ):
    m = root.modifiers.new(name=name, type='BOOLEAN')
    m.operation = operation
    m.object = object
    root.select_set(True)
    if apply:
        bpy.context.view_layer.objects.active = root
        bpy.ops.object.modifier_apply(modifier=m.name)
    return m

def create_bevel_modifier( root, name, segments=1, width=1, apply=False ):
    m = root.modifiers.new(name=name, type='BEVEL')
    m.offset_type = 'PERCENT'
    m.width_pct = width
    #m.offset_type = 'ABSOLUTE'
    #m.width = width
    m.segments = segments
    m.angle_limit = 0.523599
    root.select_set(True)
    if apply:
        bpy.context.view_layer.objects.active = root
        bpy.ops.object.modifier_apply(modifier=m.name)
    return m

def create_remesh( root, name, threshold=1, octree_depth=1, use_smooth_shade=False, apply=False):
    # Add a Remesh modifier
    remesh = root.modifiers.new(name=name, type='REMESH')
    # Set remesh properties
    remesh.mode = 'SHARP'       # Options: 'BLOCKS', 'SMOOTH', 'SHARP'
    remesh.threshold = threshold
    remesh.use_remove_disconnected = True
    remesh.use_smooth_shade = use_smooth_shade
    remesh.octree_depth = octree_depth
    if apply:
        bpy.context.view_layer.objects.active = root
        bpy.ops.object.modifier_apply(modifier=remesh.name)


    
def join( objects_to_join ):
    # List of object names you want to join
    #objects_to_join = ["Cube", "Sphere", "Cylinder"]

    # Deselect everything first
    bpy.ops.object.select_all(action='DESELECT')

    # Select objects
    for obj_name in objects_to_join:
        obj = bpy.data.objects.get(obj_name)
        if obj:
            obj.select_set(True)

    # Set the active object (the one that remains after joining)
    bpy.context.view_layer.objects.active = bpy.data.objects[objects_to_join[0]]

    # Perform join
    bpy.ops.object.join()



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
