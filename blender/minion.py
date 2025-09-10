import bpy
from math import radians

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

def create_minion( x, y, z, f ):
    radius = 1.0*f      # body radius
    height = 3.5*f      # total height (including caps)
    segments = 48     # resolution for smoothness
    # Materials
    mat_yellow = bpy.data.materials.new(name="MinionYellow")
    mat_yellow.use_nodes = True
    bsdf = mat_yellow.node_tree.nodes["Principled BSDF"]
    bsdf.inputs["Base Color"].default_value = (0.98, 0.84, 0.13, 1.0)  # yellow
    bsdf.inputs["Roughness"].default_value = 0.35

    # --- Cylinder (main body) ---
    cyl_height = height - 2 * radius
    bpy.ops.mesh.primitive_cylinder_add( vertices=segments, radius=radius, depth=cyl_height, location=(x, y, z) )
    body = bpy.context.object
    body.name = "minion_body"

    # --- Top hemisphere ---
    bpy.ops.mesh.primitive_uv_sphere_add( segments=segments, ring_count=segments // 2, radius=radius, location=(x, y, z+cyl_height/2))
    top = bpy.context.object
    top.name = "minion_top"
    # keep only top half
    bpy.ops.object.mode_set(mode='EDIT')
    bpy.ops.mesh.select_all(action='DESELECT')
    bpy.ops.mesh.bisect(plane_co=(0, 0, cyl_height + radius), plane_no=(0, 0, -1), clear_inner=True)
    bpy.ops.object.mode_set(mode='OBJECT')

    # --- Bottom hemisphere ---
    bpy.ops.mesh.primitive_uv_sphere_add( segments=segments, ring_count=segments // 2, radius=radius, location=(x, y, z-cyl_height/2))
    bottom = bpy.context.object
    bottom.name = "minion_bottom"
    # keep only bottom half
    bpy.ops.object.mode_set(mode='EDIT')
    bpy.ops.mesh.select_all(action='DESELECT')
    bpy.ops.mesh.bisect(plane_co=(0, 0, radius), plane_no=(0, 0, 1), clear_inner=True)
    bpy.ops.object.mode_set(mode='OBJECT')

    # --- Join all parts ---
    #bpy.ops.object.select_all(action='DESELECT')
    #body.select_set(True)
    #top.select_set(True)
    #bottom.select_set(True)
    #bpy.context.view_layer.objects.active = body
    #bpy.ops.object.join()

    lib.join( objects_to_join=[ "minion_body", "minion_top", "minion_bottom"])
    #lib.create_remesh( bpy.context.view_layer.objects.active, name='remesh1', octree_depth=10, apply=True )



    # Apply material
    body.data.materials.append(mat_yellow)

    # Smooth shading
    #bpy.ops.object.shade_smooth()

    print("Capsule-shaped body created ✅")
