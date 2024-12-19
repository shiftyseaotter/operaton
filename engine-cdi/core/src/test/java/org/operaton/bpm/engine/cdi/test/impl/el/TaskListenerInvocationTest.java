/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
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
package org.operaton.bpm.engine.cdi.test.impl.el;

import org.operaton.bpm.engine.cdi.test.CdiProcessEngineTestCase;
import org.operaton.bpm.engine.task.Task;
import org.operaton.bpm.engine.test.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.operaton.bpm.engine.cdi.test.impl.el.beans.CdiTaskListenerBean.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Sebastian Menski
 */
@RunWith(Arquillian.class)
public class TaskListenerInvocationTest extends CdiProcessEngineTestCase {

  @Test
  @Deployment
  public void test() {
    Map<String, Object> variables = new HashMap<>();
    variables.put(VARIABLE_NAME, INITIAL_VALUE);

    runtimeService.startProcessInstanceByKey("process", variables);

    Task task = taskService.createTaskQuery().singleResult();
    taskService.setAssignee(task.getId(), "demo");

    assertEquals(UPDATED_VALUE, taskService.getVariable(task.getId(), VARIABLE_NAME));
  }
}
