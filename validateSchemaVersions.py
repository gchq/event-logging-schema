#!/usr/bin/env python
#**********************************************************************
# Copyright 2016 Crown Copyright
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#     http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#**********************************************************************


#**********************************************************************
# Script to ensure all the version numbers in the event-logging
# XMLSchema are valid
#**********************************************************************


import os
import re
import sys
import xml.etree.ElementTree as ET

SCHEMA_FILENAME = "event-logging.xsd"

root_path = os.path.dirname(os.path.realpath(__file__))

def getMinorVersion(versionStr):
    minorVer = re.match("[0-9]*\.([0-9]*)[.-].*", versionStr).group(1)
    return minorVer

def validateVersions(newVersion, schemaFile):

    if (newVersion):
        newVersionNum = re.sub(r'^v', "", newVersion)
        print "Gradle build version: %s" % newVersionNum

    if (not schemaFile):
        schemaFile = SCHEMA_FILENAME

    if (not os.path.isfile(schemaFile)):
        print "ERROR - Schema file %s doesn't exist" % schemaFile
        exit(1)

    print ""
    print "Validating file %s" % schemaFile

    # pattern = re.compile("xmlns:evt\"event-logging:.*\"")
    xsdFile = open(schemaFile, 'r')
    filetext = xsdFile.read()
    xsdFile.close()
    matches = re.findall("xmlns:evt=\"event-logging:(.*)\"", filetext)
    if (len(matches) != 1):
        raise ValueError("Unexpected matches for evt namespace", matches)
    namespaceVersion = matches[0]
    print "namespace version: %s" % namespaceVersion

    xml_root = ET.parse(schemaFile).getroot()

    targetNamespaceAttr = xml_root.get("targetNamespace")
    targetNamespaceVersion = re.match(".*:(.*)$", targetNamespaceAttr).group(1)
    print "targetNamespace: %s" % targetNamespaceVersion

    versionAttrVersion = xml_root.get("version")
    print "version: %s" % versionAttrVersion

    idAttr = xml_root.get("id")
    idAttrVersion = re.match("event-logging-v?(.*)$", idAttr).group(1)
    print "id: %s" % idAttrVersion

    ns = {'xs': 'http://www.w3.org/2001/XMLSchema'}
    enumVersions = []
    print "Version enumerations:"
    for enumElm in xml_root.findall("./xs:simpleType[@name='VersionSimpleType']/xs:restriction/xs:enumeration", ns):
        print "  %s" % enumElm.get("value")
        enumVersions.append(enumElm.get("value"))

    print ""
    versionRegex = "[0-9]+\.[0-9]+(\.[0-9]+|-(alpha|beta)\.[0-9]+)"

    if (newVersion and not re.match(".*SNAPSHOT", newVersionNum)):
        if (versionAttrVersion != newVersionNum):
            raise ValueError("version attribute and planned version do not match", versionAttrVersion, newVersionNum)

        if (not re.match(versionRegex, versionAttrVersion)):
            raise ValueError("version attribute does not match the valid regex", versionAttrVersion, versionRegex)

        if (not versionAttrVersion.startswith(targetNamespaceVersion)):
            raise ValueError("Major version of the version attribute does not match the targetNamespace version", versionAttrVersion, targetNamespaceVersion)

        minorVer = getMinorVersion(versionAttrVersion)

        for enumVer in enumVersions:
            if (not enumVer == "SNAPSHOT"):
                if (not enumVer.startswith(targetNamespaceVersion)):
                    raise ValueError("Major version of the enumeration version does not match the targetNamespace version", enumVer, targetNamespaceVersion)
                minorVerOfEnum = getMinorVersion(enumVer)

                if (minorVerOfEnum > minorVer):
                    raise ValueError("Minor version of enumeration version is higher than the minor version of the schema version. Should be less than or equal to the schema version", enumVer, versionAttrVersion)

    # print enumVersions

    if (namespaceVersion != targetNamespaceVersion):
        raise ValueError("namespace version and targetNamespace version do not match", namespaceVersion, targetNamespaceVersion)

    if (versionAttrVersion != idAttrVersion):
        raise ValueError("version attribute and id attribute do not match", versionAttrVersion, idAttrVersion)

    if (not versionAttrVersion in enumVersions):
        raise ValueError("Schema version is not in the list of version enumerations", versionAttrVersion, enumVersions)




# Script starts here
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
print sys.argv

if len(sys.argv) == 2:
    newVersion = sys.argv[1]
    schemaFile = None
elif len(sys.argv) == 3:
    newVersion = sys.argv[1]
    schemaFile = sys.argv[2]
else:
    newVersion = None
    schemaFile = None

print "version [%s], schema file [%s]" % (newVersion, schemaFile)
    
validateVersions(newVersion, schemaFile)

print ""
print "Done!"
exit(0)
