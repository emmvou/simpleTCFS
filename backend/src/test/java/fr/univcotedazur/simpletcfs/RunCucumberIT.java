package fr.univcotedazur.simpletcfs;

import org.junit.platform.suite.api.*;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("fr/univcotedazur/simpletcfs/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "fr.univcotedazur.simpletcfs.features")
public class RunCucumberIT {
}
