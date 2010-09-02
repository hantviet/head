/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.application.questionnaire.migration;

import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.questionnaire.migration.mappers.QuestionnaireMigrationMapper;
import org.mifos.customers.surveys.business.Survey;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireMigration {

    @Autowired
    private QuestionnaireMigrationMapper questionnaireMigrationMapper;

    @Autowired
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @SuppressWarnings({"UnusedDeclaration"})
    public QuestionnaireMigration() {
        // used for Spring wiring
    }

    public QuestionnaireMigration(QuestionnaireMigrationMapper questionnaireMigrationMapper, QuestionnaireServiceFacade questionnaireServiceFacade) {
        this.questionnaireMigrationMapper = questionnaireMigrationMapper;
        this.questionnaireServiceFacade = questionnaireServiceFacade;
    }

    public Integer migrate(List<CustomFieldDefinitionEntity> customFields) {
        QuestionGroupDto questionGroupDto = questionnaireMigrationMapper.map(customFields);
        return questionnaireServiceFacade.createQuestionGroup(questionGroupDto);
    }

    public List<Integer> migrateSurveys(List<Survey> surveys) {
        List<Integer> questionGroupIds = new ArrayList<Integer>();
        for (Survey survey : surveys) {
            QuestionGroupDto questionGroupDto = questionnaireMigrationMapper.map(survey);
            Integer questionGroupId = questionnaireServiceFacade.createQuestionGroup(questionGroupDto);
            questionGroupIds.add(questionGroupId);
        }
        return questionGroupIds;
    }
}