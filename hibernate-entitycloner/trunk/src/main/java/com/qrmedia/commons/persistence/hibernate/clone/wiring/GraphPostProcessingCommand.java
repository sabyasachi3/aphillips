/*
 * @(#)GraphPostProcessingCommand.java     17 Feb 2009
 * 
 * Copyright © 2009 Andrew Phillips.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.qrmedia.commons.persistence.hibernate.clone.wiring;

/**
 * An action to be executed <u>once</u> the entity graph has been wired up.
 * <p>
 * This may, for instance, be an action to remove wrappers used during the cloning,
 * replace proxies with their target objects etc. 
 *
 * @author anph
 * @since 17 Feb 2009
 * @see GraphWiringCommand
 */
public interface GraphPostProcessingCommand {

    /**
     * Carries out the post-processing.
     */
    void execute();
}
