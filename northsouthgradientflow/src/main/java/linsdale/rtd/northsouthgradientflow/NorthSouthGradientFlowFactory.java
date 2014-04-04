/*
 * Copyright (C) 2014 Richard Linsdale (richard.linsdale at blueyonder.co.uk)
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package linsdale.rtd.northsouthgradientflow;

import linsdale.rtd.core.api.FlowModel;
import linsdale.rtd.core.api.RtdFactory;
import linsdale.rtd.code.DefFileModel;
import org.openide.util.lookup.ServiceProvider;

/**
 * A Factory to create NorthSouthGradientFlow Objects
 *
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
@ServiceProvider(service = NorthSouthGradientFlowFactory.class)
public class NorthSouthGradientFlowFactory implements RtdFactory<FlowModel> {

    @Override
    public FlowModel newInstance(String name, DefFileModel dfm) {
        return new NorthSouthGradientFlow(name, dfm);
    }
}
