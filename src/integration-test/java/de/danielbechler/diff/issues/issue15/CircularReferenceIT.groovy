/*
 * Copyright 2014 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the 'License')
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.issues.issue15

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.mock.ObjectWithCircularReference
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
public class CircularReferenceIT extends Specification {

	def 'circular reference'() {
		given:
		  def workingA = new ObjectWithCircularReference('a')
		  def workingB = new ObjectWithCircularReference('b')
		  workingA.reference = workingB
		  workingB.reference = workingA
		and:
		  def baseA = new ObjectWithCircularReference('a')
		  def baseB = new ObjectWithCircularReference('c')
		  baseA.reference = baseB
		  baseB.reference = baseA
		when:
		  def diffRoot = ObjectDifferBuilder.buildDefault().compare(workingA, baseA)
		then:
		  def circularNode = diffRoot.getChild(NodePath.with('reference', 'reference'))
		  circularNode.isCircular()
		  circularNode.circleStartPath == NodePath.withRoot()
		  circularNode.circleStartNode.is(diffRoot)
	}

	// FIXME: I feel like this test doesn't add much more than the other one. Delete?
	def 'circular reference should be added when enabled in configuration'() {
		given:
		  def workingA = new ObjectWithCircularReference('a')
		  def workingB = new ObjectWithCircularReference('b')
		  def workingC = new ObjectWithCircularReference('c')
		  workingA.reference = workingB
		  workingB.reference = workingC
		  workingC.reference = workingA
		and:
		  def baseA = new ObjectWithCircularReference('a')
		  def baseB = new ObjectWithCircularReference('b')
		  def baseC = new ObjectWithCircularReference('d')
		  baseA.reference = baseB
		  baseB.reference = baseC
		  baseC.reference = baseA
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(workingA, baseA)
		then:
		  node.getChild(NodePath.with('reference', 'reference', 'reference')).isCircular()
	}
}
