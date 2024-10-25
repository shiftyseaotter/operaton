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
package org.operaton.bpm.run.test.config.identity;

import static org.assertj.core.api.Assertions.assertThat;

import org.operaton.bpm.engine.impl.plugin.AdministratorAuthorizationPlugin;
import org.operaton.bpm.run.OperatonBpmRun;
import org.operaton.bpm.run.property.OperatonBpmRunAdministratorAuthorizationProperties;
import org.operaton.bpm.run.property.OperatonBpmRunProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { OperatonBpmRun.class })
@ActiveProfiles(profiles = { "test-auth-enabled" , "test-admin-auth-enabled" })
public class AdminAuthorizationConfigurationTest {

  @Autowired
  protected AdministratorAuthorizationPlugin authorizationPlugin;

  @Autowired
  protected OperatonBpmRunProperties properties;

  @Test
  public void shouldPickUpConfiguration() {
    // given
    OperatonBpmRunAdministratorAuthorizationProperties adminProps = properties.getAdminAuth();

    // then
    assertThat(adminProps.isEnabled()).isTrue();
    assertThat(adminProps.getAdministratorGroupName()).isEqualTo(authorizationPlugin.getAdministratorGroupName());
    assertThat(adminProps.getAdministratorUserName()).isEqualTo(authorizationPlugin.getAdministratorUserName());
  }

}