/*
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
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
package org.neo4j.cypher.internal.runtime.interpreted.pipes

import org.neo4j.cypher.internal.runtime.interpreted.QueryStateHelper
import org.neo4j.cypher.internal.runtime.interpreted.ValueComparisonHelper._
import org.neo4j.cypher.internal.v4_0.util.test_helpers.CypherFunSuite
import org.neo4j.values.storable.Values

class ApplyPipeTest extends CypherFunSuite with PipeTestSupport {

  test("should work by applying the identity operator on the rhs") {
    val lhsData = List(Map("a" -> 1), Map("a" -> 2))
    val lhs = new FakePipe(lhsData.iterator)
    val rhs = pipeWithResults { state => Iterator(state.initialContext.get) }

    val result = ApplyPipe(lhs, rhs)().createResults(QueryStateHelper.empty).toList

    result should beEquivalentTo(lhsData)
  }

  test("should work by applying a  on the rhs") {
    val lhsData = List(Map("a" -> 1, "b" -> 3), Map("a" -> 2, "b" -> 4))
    val lhs = new FakePipe(lhsData.iterator)
    val rhsData = "c" -> Values.intValue(36)
    val rhs = pipeWithResults { state =>
      state.initialContext.get.set(rhsData._1, rhsData._2)
      Iterator(state.initialContext.get)
    }

    val result = ApplyPipe(lhs, rhs)().createResults(QueryStateHelper.empty).toList

    result should beEquivalentTo(lhsData.map(_ + rhsData))
  }
}
