/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.camunda.bpm.engine.test.concurrency.partitioning;

import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ByteArrayEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.camunda.bpm.engine.impl.persistence.entity.VariableInstanceEntity;
import org.camunda.bpm.engine.variable.Variables;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Tassilo Weidner
 */

public class CompetingHistoricByteArrayPartitioningTest extends AbstractPartitioningTest {

  final protected String VARIABLE_NAME = "aVariableName";
  final protected String VARIABLE_VALUE = "aVariableValue";
  final protected String ANOTHER_VARIABLE_VALUE = "anotherVariableValue";

  public void testConcurrentFetchAndDelete() {
    // given
    final String processInstanceId = deployAndStartProcess(PROCESS_WITH_USERTASK,
      Variables.createVariables().putValue(VARIABLE_NAME,
        Variables.byteArrayValue(VARIABLE_VALUE.getBytes())))
      .getId();

    final String[] historicByteArrayId = new String[1];
    commandExecutor.execute(new Command<Void>() {
      public Void execute(CommandContext commandContext) {

        ExecutionEntity execution = commandContext.getExecutionManager().findExecutionById(processInstanceId);

        VariableInstanceEntity varInstance = (VariableInstanceEntity) execution.getVariableInstance(VARIABLE_NAME);
        HistoricVariableInstanceEntity historicVariableInstance = commandContext.getHistoricVariableInstanceManager()
          .findHistoricVariableInstanceByVariableInstanceId(varInstance.getId());

        historicByteArrayId[0] = historicVariableInstance.getByteArrayValueId();

        return null;
      }
    });

    ThreadControl asyncThread = executeControllableCommand(new AsyncThread(processInstanceId, historicByteArrayId[0]));

    asyncThread.waitForSync();

    commandExecutor.execute(new Command<Void>() {
      public Void execute(CommandContext commandContext) {

        commandContext.getByteArrayManager()
          .deleteByteArrayById(historicByteArrayId[0]);

        return null;
      }
    });

    commandExecutor.execute(new Command<Void>() {
      public Void execute(CommandContext commandContext) {

        // assume
        assertThat(commandContext.getDbEntityManager().selectById(ByteArrayEntity.class, historicByteArrayId[0]), nullValue());

        return null;
      }
    });

    // when
    asyncThread.makeContinue();
    asyncThread.waitUntilDone();

    // then
    assertThat(runtimeService.createVariableInstanceQuery().singleResult().getName(), is(VARIABLE_NAME));
    assertThat(new String((byte[]) runtimeService.createVariableInstanceQuery().singleResult().getValue()), is(ANOTHER_VARIABLE_VALUE));
  }

  public class AsyncThread extends ControllableCommand<Void> {

    String processInstanceId;
    String historicByteArrayId;

    AsyncThread(String processInstanceId, String historicByteArrayId) {
      this.processInstanceId = processInstanceId;
      this.historicByteArrayId = historicByteArrayId;
    }

    public Void execute(CommandContext commandContext) {
      commandContext.getDbEntityManager()
        .selectById(ByteArrayEntity.class, historicByteArrayId); // cache

      monitor.sync();

      runtimeService.setVariable(processInstanceId, VARIABLE_NAME,
        Variables.byteArrayValue(ANOTHER_VARIABLE_VALUE.getBytes()));

      return null;
    }

  }

}
