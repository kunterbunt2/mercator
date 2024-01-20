package de.bushnaq.abdalla.mercator.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;

public class MeshMerger {

	public static Mesh copyMesh(final Mesh meshToCopy, final boolean isStatic, final boolean removeDuplicates, final int[] usage) {
		// TODO move this to a copy constructor?
		// TODO duplicate the buffers without double copying the data if possible.
		// TODO perhaps move this code to JNI if it turns out being too slow.
		final int vertexSize = meshToCopy.getVertexSize() / 4;
		int numVertices = meshToCopy.getNumVertices();
		float[] vertices = new float[numVertices * vertexSize];
		meshToCopy.getVertices(0, vertices.length, vertices);
		short[] checks = null;
		VertexAttribute[] attrs = null;
		int newVertexSize = 0;
		if (usage != null) {
			int size = 0;
			int as = 0;
			for (int i = 0; i < usage.length; i++)
				if (meshToCopy.getVertexAttribute(usage[i]) != null) {
					size += meshToCopy.getVertexAttribute(usage[i]).numComponents;
					as++;
				}
			if (size > 0) {
				attrs = new VertexAttribute[as];
				checks = new short[size];
				int idx = -1;
				int ai = -1;
				for (int i = 0; i < usage.length; i++) {
					final VertexAttribute a = meshToCopy.getVertexAttribute(usage[i]);
					if (a == null)
						continue;
					for (int j = 0; j < a.numComponents; j++)
						checks[++idx] = (short) (a.offset / 4 + j);
					attrs[++ai] = new VertexAttribute(a.usage, a.numComponents, a.alias);
					newVertexSize += a.numComponents;
				}
			}
		}
		if (checks == null) {
			checks = new short[vertexSize];
			for (short i = 0; i < vertexSize; i++)
				checks[i] = i;
			newVertexSize = vertexSize;
		}

		final int numIndices = meshToCopy.getNumIndices();
		short[] indices = null;
		if (numIndices > 0) {
			indices = new short[numIndices];
			meshToCopy.getIndices(indices);
			if (removeDuplicates || newVertexSize != vertexSize) {
				final float[] tmp = new float[vertices.length];
				int size = 0;
				for (int i = 0; i < numIndices; i++) {
					final int idx1 = indices[i] * vertexSize;
					short newIndex = -1;
					if (removeDuplicates) {
						for (short j = 0; j < size && newIndex < 0; j++) {
							final int idx2 = j * newVertexSize;
							boolean found = true;
							for (int k = 0; k < checks.length && found; k++) {
								if (tmp[idx2 + k] != vertices[idx1 + checks[k]])
									found = false;
							}
							if (found)
								newIndex = j;
						}
					}
					if (newIndex > 0)
						indices[i] = newIndex;
					else {
						final int idx = size * newVertexSize;
						for (int j = 0; j < checks.length; j++) {
							System.out.println(idx + j);
							tmp[idx + j] = vertices[idx1 + checks[j]];
						}
						indices[i] = (short) size;
						size++;
					}
				}
				vertices = tmp;
				numVertices = size;
			}
		}

		Mesh result;
		if (attrs == null)
			result = new Mesh(isStatic, numVertices, indices == null ? 0 : indices.length, meshToCopy.getVertexAttributes());
		else
			result = new Mesh(isStatic, numVertices, indices == null ? 0 : indices.length, attrs);
		result.setVertices(vertices, 0, numVertices * newVertexSize);
		result.setIndices(indices);
		return result;
	}

	public static Mesh mergeMeshes(final AbstractList<Mesh> meshes, final AbstractList<Matrix4> transformations) {
		if (meshes.size() == 0)
			return null;

		int vertexArrayTotalSize = 0;
		int indexArrayTotalSize = 0;

		final VertexAttributes va = meshes.get(0).getVertexAttributes();
		final int vaA[] = new int[va.size()];
		for (int i = 0; i < va.size(); i++) {
			vaA[i] = va.get(i).usage;
		}

		for (int i = 0; i < meshes.size(); i++) {
			final Mesh mesh = meshes.get(i);
			if (mesh.getVertexAttributes().size() != va.size()) {
				meshes.set(i, copyMesh(mesh, true, false, vaA));
			}

			vertexArrayTotalSize += mesh.getNumVertices() * mesh.getVertexSize() / 4;
			indexArrayTotalSize += mesh.getNumIndices();
		}

		final float vertices[] = new float[vertexArrayTotalSize];
		final short indices[] = new short[indexArrayTotalSize];

		int indexOffset = 0;
		int vertexOffset = 0;
		int vertexSizeOffset = 0;
		int vertexSize = 0;

		for (int i = 0; i < meshes.size(); i++) {
			final Mesh mesh = meshes.get(i);

			final int numIndices = mesh.getNumIndices();
			final int numVertices = mesh.getNumVertices();
			vertexSize = mesh.getVertexSize() / 4;
			final int baseSize = numVertices * vertexSize;
			final VertexAttribute posAttr = mesh.getVertexAttribute(Usage.Position);
			final int offset = posAttr.offset / 4;
			final int numComponents = posAttr.numComponents;

			//			if ( mesh.getVertexSize() == 48 )
			//			{
			//				System.out.println( "Break" );
			//			}

			{ // uzupelnianie tablicy indeksow
				mesh.getIndices(indices, indexOffset);
				for (int c = indexOffset; c < (indexOffset + numIndices); c++) {
					indices[c] += vertexOffset;
				}
				indexOffset += numIndices;
			}

			mesh.getVertices(0, baseSize, vertices, vertexSizeOffset);
			Mesh.transform(transformations.get(i), vertices, vertexSize, offset, numComponents, vertexOffset, numVertices);
			vertexOffset += numVertices;
			vertexSizeOffset += baseSize;
		}

		final Mesh result = new Mesh(true, vertexOffset, indices.length, meshes.get(0).getVertexAttributes());
		result.setVertices(vertices);
		result.setIndices(indices);
		return result;
	}

	public static ModelInstance optimizeModels(final List<ModelInstance> renderModelInstances, final Material material) {
		final ArrayList<com.badlogic.gdx.graphics.Mesh> meshesToMerge = new ArrayList<com.badlogic.gdx.graphics.Mesh>();
		final ArrayList<Matrix4> transforms = new ArrayList<Matrix4>();

		for (final ModelInstance modelInstance : renderModelInstances) {
			final com.badlogic.gdx.graphics.Mesh mesh = modelInstance.model.meshes.get(0);

			if (mesh == null)
				continue;

			meshesToMerge.add(mesh);
			transforms.add(modelInstance.transform);
		}

		final Mesh uberMesh = mergeMeshes(meshesToMerge, transforms);
		if (uberMesh != null) {
			final ModelBuilder builder = new ModelBuilder();
			builder.begin();
			builder.part("0", uberMesh, GL20.GL_TRIANGLES, material);

			//	Model uberModel = ModelBuilder.createFromMesh( uberMesh, GL20.GL_TRIANGLES, material );
			return new ModelInstance(builder.end());

		}
		return null;

	}

}
