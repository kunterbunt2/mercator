/*
 * Copyright (C) 2024 Abdalla Bushnaq
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bushnaq.abdalla.mercator.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author kunterbunt
 */
public class MavenPropertiesProvider {
    private static final MavenPropertiesProvider INSTANCE = new MavenPropertiesProvider();// ---initialize the static field
    static               ResourceBundle          rb;

    private MavenPropertiesProvider() {
        try {
            MavenPropertiesProvider.rb = ResourceBundle.getBundle("maven");
        } catch (MissingResourceException e) {
            // ---rb stays null
        }
    }

    public static String getProperty(String name) throws Exception {
        if (MavenPropertiesProvider.rb == null) {
            throw new Exception("Resource bundle 'maven' was not found or error while reading current version.");
        }
        return MavenPropertiesProvider.rb.getString(name);
    }
}
