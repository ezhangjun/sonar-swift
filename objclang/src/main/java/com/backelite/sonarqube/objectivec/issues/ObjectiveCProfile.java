/**
 * Swift SonarQube Plugin - Objective-C module - Enables analysis of Swift and Objective-C projects into SonarQube.
 * Copyright © 2015 Backelite (${email})
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.backelite.sonarqube.objectivec.issues;

import com.backelite.sonarqube.objectivec.issues.fauxpas.FauxPasProfile;
import com.backelite.sonarqube.objectivec.issues.fauxpas.FauxPasProfileImporter;
import com.backelite.sonarqube.objectivec.issues.oclint.OCLintProfile;
import com.backelite.sonarqube.objectivec.issues.oclint.OCLintProfileImporter;
import com.backelite.sonarqube.objectivec.lang.core.ObjectiveC;
import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.utils.ValidationMessages;

import java.io.InputStreamReader;
import java.io.Reader;

public class ObjectiveCProfile extends ProfileDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectiveCProfile.class);

    private final OCLintProfileImporter ocLintProfileImporter;
    private final FauxPasProfileImporter fauxPasProfileImporter;

    public ObjectiveCProfile(final OCLintProfileImporter ocLintProfileImporter, final FauxPasProfileImporter fauxPasProfileImporter) {
        this.ocLintProfileImporter = ocLintProfileImporter;
        this.fauxPasProfileImporter = fauxPasProfileImporter;
    }

    @Override
    public RulesProfile createProfile(ValidationMessages messages) {


        LOGGER.info("Creating Objective-C Profile");

        Reader config = null;
        final RulesProfile profile = RulesProfile.create("Objective-C", ObjectiveC.KEY);
        profile.setDefaultProfile(true);

        try {
            config = new InputStreamReader(getClass().getResourceAsStream(OCLintProfile.PROFILE_PATH));
            RulesProfile ocLintRulesProfile = ocLintProfileImporter.importProfile(config, messages);
            for (ActiveRule rule : ocLintRulesProfile.getActiveRules()) {
                profile.addActiveRule(rule);
            }

            config = new InputStreamReader(getClass().getResourceAsStream(FauxPasProfile.PROFILE_PATH));
            RulesProfile fauxPasRulesProfile = fauxPasProfileImporter.importProfile(config, messages);
            for (ActiveRule rule : fauxPasRulesProfile.getActiveRules()) {
                profile.addActiveRule(rule);
            }


            return profile;
        } finally {

            Closeables.closeQuietly(config);
        }
    }

}
