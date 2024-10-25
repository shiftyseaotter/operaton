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
package org.operaton.bpm.container.impl.deployment.jobexecutor;

import java.util.Set;
import org.operaton.bpm.container.impl.ContainerIntegrationLogger;
import org.operaton.bpm.container.impl.spi.PlatformServiceContainer;
import org.operaton.bpm.container.impl.spi.DeploymentOperation;
import org.operaton.bpm.container.impl.spi.DeploymentOperationStep;
import org.operaton.bpm.container.impl.spi.ServiceTypes;
import org.operaton.bpm.engine.impl.ProcessEngineLogger;

/**
 * <p>Deployment operation step responsible for stopping all job acquisitions</p>
 *
 * @author Daniel Meyer
 *
 */
public class StopJobExecutorStep extends DeploymentOperationStep {

  protected final static ContainerIntegrationLogger LOG = ProcessEngineLogger.CONTAINER_INTEGRATION_LOGGER;

  public String getName() {
    return "Stop managed job acquisitions";
  }

  public void performOperationStep(DeploymentOperation operationContext) {

    final PlatformServiceContainer serviceContainer = operationContext.getServiceContainer();

    Set<String> jobExecutorServiceNames = serviceContainer.getServiceNames(ServiceTypes.JOB_EXECUTOR);

    for (String serviceName : jobExecutorServiceNames) {
      try {
        serviceContainer.stopService(serviceName);
      }
      catch(Exception e) {
        LOG.exceptionWhileStopping("Job Executor Service", serviceName, e);
      }
    }

  }

}