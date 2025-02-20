/*
 * Copyright 2011-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vn.com.unit.springframework.data.mirage.repository.query;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.core.MethodParameter;
import org.springframework.data.repository.query.Parameters;

import vn.com.unit.sparwings.spring.data.chunk.Chunkable;

/**
 * TODO for daisuke
 * 
 * @since TODO for daisuke
 * @version $Id$
 * @author daisuke
 */
public class ChunkableSupportedParameters
		extends Parameters<ChunkableSupportedParameters, ChunkableSupportedParameter> {
	
    private final int chunkableIndex;

    /**
     * Creates a new {@link ChunkableSupportedParameters} instance from the given {@link Method}.
     *
     * @param method must not be {@literal null}.
     */
    public ChunkableSupportedParameters(Method method) {
        super(initializeParameters(method));
        this.chunkableIndex = findChunkableIndex(method);
    }
    private static List<ChunkableSupportedParameter> initializeParameters(Method method) {
        return IntStream.range(0, method.getParameterCount())
                .mapToObj(i -> new ChunkableSupportedParameter(new MethodParameter(method, i)))
                .toList();
    }
    private ChunkableSupportedParameters(List<ChunkableSupportedParameter> originals) {
        super(originals);
        this.chunkableIndex = findChunkableIndex(originals);
    }

    /**
     * Returns the index of the {@link Chunkable} {@link Method} parameter if available. Will return {@literal -1} if there
     * is no {@link Chunkable} argument in the {@link Method}'s parameter list.
     *
     * @return the index of the {@link Chunkable} parameter, or -1 if not present.
     */
    public int getChunkableIndex() {
        return chunkableIndex;
    }

    /**
     * Returns whether the method the {@link Parameters} was created for contains a {@link Chunkable} argument.
     *
     * @return {@code true} if a {@link Chunkable} parameter exists, {@code false} otherwise.
     */
    public boolean hasChunkableParameter() {
        return chunkableIndex != -1;
    }

    @Override
    protected ChunkableSupportedParameters createFrom(List<ChunkableSupportedParameter> parameters) {
        return new ChunkableSupportedParameters(parameters);
    }

    protected ChunkableSupportedParameter createParameter(MethodParameter parameter) {
        return new ChunkableSupportedParameter(parameter);
    }

    /**
     * Finds the index of the {@link Chunkable} parameter in the given {@link Method}.
     *
     * @param method the method to inspect.
     * @return the index of the {@link Chunkable} parameter, or -1 if not present.
     */
    private static int findChunkableIndex(Method method) {
        List<Class<?>> parameterTypes = List.of(method.getParameterTypes());
        return IntStream.range(0, parameterTypes.size())
                .filter(i -> Chunkable.class.isAssignableFrom(parameterTypes.get(i)))
                .findFirst()
                .orElse(-1);
    }

    /**
     * Finds the index of the {@link Chunkable} parameter in the given list of parameters.
     *
     * @param parameters the list of {@link ChunkableSupportedParameter}.
     * @return the index of the {@link Chunkable} parameter, or -1 if not present.
     */
    private static int findChunkableIndex(List<ChunkableSupportedParameter> parameters) {
        return IntStream.range(0, parameters.size())
                .filter(i -> parameters.get(i).isChunkable())
                .findFirst()
                .orElse(-1);
    }
}
