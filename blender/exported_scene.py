import bpy
bpy.ops.object.select_all(action='SELECT')
bpy.ops.object.delete()  # Clear scene

bpy.ops.mesh.primitive_cube_add(location=(0.0, 0.0, 0.0), rotation=(0.0, 0.0, 0.0), scale=(0.5, 0.5, 0.5))
bpy.context.object.name = 'Cube'
obj = bpy.context.active_object
mod = obj.modifiers.new(name="Bevel", type="BEVEL")
mod.width = 0.019999999552965164
mod.segments = 1
mod.profile = 0.5

# Material: material.container.plane
mat = bpy.data.materials.new(name='material.container.plane')
mat.use_nodes = True
principled_bsdf = mat.node_tree.nodes['Principled BSDF']
principled_bsdf.inputs['Base Color'].default_value = (1.0, 1.0, 1.0, 1.0)
principled_bsdf.inputs['Metallic'].default_value = 0.699999988079071
principled_bsdf.inputs['Roughness'].default_value = 0.20000000298023224
# No Specular input found, skipping it.
obj.data.materials.append(mat)


