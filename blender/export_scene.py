# Blender Scene Export Script

import bpy
import os

# Output path (change this to where you want to save the generated script)
output_path = bpy.path.abspath("//scene.py")

def write_line(file, line):
    file.write(line + "\n")

def get_modifier_code(obj):
    lines = []
    for mod in obj.modifiers:
        lines.append(f'obj = bpy.context.active_object')
        lines.append(f'mod = obj.modifiers.new(name="{mod.name}", type="{mod.type}")')
        
        # Add modifier-specific settings
        if mod.type == 'SUBSURF':
            lines.append(f'mod.levels = {mod.levels}')
            lines.append(f'mod.render_levels = {mod.render_levels}')
        elif mod.type == 'BEVEL':
            lines.append(f'mod.width = {mod.width}')
            lines.append(f'mod.segments = {mod.segments}')
            lines.append(f'mod.profile = {mod.profile}')
        elif mod.type == 'BOOLEAN':
            if mod.object:
                lines.append(f'# Boolean modifier references object: {mod.object.name}')
                lines.append(f'# You must manually ensure this object exists in scene.')
                lines.append(f'mod.object = bpy.data.objects["{mod.object.name}"]')
                lines.append(f'mod.operation = "{mod.operation}"')
        # Add other modifiers here as needed...
        lines.append('')  # empty line for spacing
    return lines

def get_material_code(obj):
    lines = []
    # Loop through the object material slots
    for material_slot in obj.material_slots:
        material = material_slot.material
        if material and material.use_nodes:
            # Check if material uses nodes and if it has a Principled BSDF
            bsdf_node = None
            for node in material.node_tree.nodes:
                if node.type == 'BSDF_PRINCIPLED':
                    bsdf_node = node
                    break

            if bsdf_node:
                lines.append(f"# Material: {material.name}")
                lines.append(f"mat = bpy.data.materials.new(name='{material.name}')")
                lines.append(f"mat.use_nodes = True")
                lines.append(f"principled_bsdf = mat.node_tree.nodes['Principled BSDF']")

                # Capture Principled BSDF properties
                lines.append(f"principled_bsdf.inputs['Base Color'].default_value = {tuple(bsdf_node.inputs['Base Color'].default_value)}")
                lines.append(f"principled_bsdf.inputs['Metallic'].default_value = {bsdf_node.inputs['Metallic'].default_value}")
                lines.append(f"principled_bsdf.inputs['Roughness'].default_value = {bsdf_node.inputs['Roughness'].default_value}")
                
                # Only access Specular if it exists
                if 'Specular' in bsdf_node.inputs:
                    lines.append(f"principled_bsdf.inputs['Specular'].default_value = {bsdf_node.inputs['Specular'].default_value}")
                else:
                    lines.append(f"# No Specular input found, skipping it.")

                lines.append(f"obj.data.materials.append(mat)")
                lines.append("")
    return lines

with open(output_path, "w") as f:
    write_line(f, "import bpy")
    write_line(f, "bpy.ops.object.select_all(action='SELECT')")
    write_line(f, "bpy.ops.object.delete()  # Clear scene\n")

    for obj in bpy.data.objects:
        if obj.type != 'MESH':
            continue

        primitive_created = False

        # Try to guess primitive type from name (very basic)
        name_lower = obj.name.lower()
        location = tuple(round(c, 4) for c in obj.location)
        rotation = tuple(round(a, 4) for a in obj.rotation_euler)
        scale = tuple(round(s, 4) for s in obj.scale)

        if "cube" in name_lower:
            write_line(f, f"bpy.ops.mesh.primitive_cube_add(location={location}, rotation={rotation}, scale={scale})")
            primitive_created = True
        elif "sphere" in name_lower:
            write_line(f, f"bpy.ops.mesh.primitive_uv_sphere_add(location={location}, rotation={rotation}, scale={scale})")
            primitive_created = True
        elif "cylinder" in name_lower:
            write_line(f, f"bpy.ops.mesh.primitive_cylinder_add(location={location}, rotation={rotation}, scale={scale})")
            primitive_created = True
        elif "cone" in name_lower:
            write_line(f, f"bpy.ops.mesh.primitive_cone_add(location={location}, rotation={rotation}, scale={scale})")
            primitive_created = True
        elif "plane" in name_lower:
            write_line(f, f"bpy.ops.mesh.primitive_plane_add(location={location}, rotation={rotation}, scale={scale})")
            primitive_created = True

        if not primitive_created:
            # Fallback: create an empty mesh (does not preserve geometry!)
            write_line(f, f"# Unknown primitive: {obj.name}, exporting as empty mesh")
            write_line(f, f"mesh = bpy.data.meshes.new(name='{obj.name}_mesh')")
            write_line(f, f"obj = bpy.data.objects.new('{obj.name}', mesh)")
            write_line(f, f"bpy.context.collection.objects.link(obj)")
            write_line(f, f"obj.location = {location}")
            write_line(f, f"obj.rotation_euler = {rotation}")
            write_line(f, f"obj.scale = {scale}")
            write_line(f, f"bpy.context.view_layer.objects.active = obj")
        else:
            write_line(f, f"bpy.context.object.name = '{obj.name}'")

        # Write modifiers
        modifier_lines = get_modifier_code(obj)
        for line in modifier_lines:
            write_line(f, line)

        # Materials (Principled BSDF)
        material_lines = get_material_code(obj)
        for line in material_lines:
            write_line(f, line)

        # Parenting
        if obj.parent:
            write_line(f, f"# Parenting: {obj.name} to {obj.parent.name}")
            write_line(f, f"obj.parent = bpy.data.objects[\"{obj.parent.name}\"]")

        write_line(f, "")  # blank line between objects

print(f"Scene exported to: {output_path}")
