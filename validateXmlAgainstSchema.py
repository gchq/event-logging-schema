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
# Script to validate all xml files found in the passed directory
# against the passed XMLSchema file
#**********************************************************************

import os
import re
import sys
import xml.etree.ElementTree as ET

USAGE_TXT = "Usage:\validateXmlAgainstSchema.py xmlSchemaFile xmlDirOrFileName"

root_path = os.path.dirname(os.path.realpath(__file__))
def print_usage():
    print(USAGE_TXT)
    print("\n")





def getMinorVersion(versionStr):
    minorVer = re.match("[0-9]*\.([0-9]*)\..*", versionStr).group(1)
    return minorVer

def validateVersions(newVersion):
    print "Validating file %s" % SCHEMA_FILENAME

    print ""

    if (newVersion):
        newVersionNum = re.sub(r'^v', "", newVersion)
        print "Gradle build version: %s" % newVersionNum

    # pattern = re.compile("xmlns:evt\"event-logging:.*\"")
    xsdFile = open(SCHEMA_FILENAME, 'r')
    filetext = xsdFile.read()
    xsdFile.close()
    matches = re.findall("xmlns:evt=\"event-logging:(.*)\"", filetext)
    if (len(matches) != 1):
        raise ValueError("Unexpected matches for evt namespace", matches)
    namespaceVersion = matches[0]
    print "namespace version: %s" % namespaceVersion

    xml_root = ET.parse(SCHEMA_FILENAME).getroot()

    targetNamespaceAttr = xml_root.get("targetNamespace")
    targetNamespaceVersion = re.match(".*:(.*)$", targetNamespaceAttr).group(1)
    print "targetNamespace: %s" % targetNamespaceVersion

    versionAttrVersion = xml_root.get("version")
    print "version: %s" % versionAttrVersion

    idAttr = xml_root.get("id")
    idAttrVersion = re.match("event-logging-v(.*)$", idAttr).group(1)
    print "id: %s" % idAttrVersion

    ns = {'xs': 'http://www.w3.org/2001/XMLSchema'}
    enumVersions = []
    print "Version enumerations:"
    for enumElm in xml_root.findall("./xs:simpleType[@name='VersionSimpleType']/xs:restriction/xs:enumeration", ns):
        print "  %s" % enumElm.get("value")
        enumVersions.append(enumElm.get("value"))

    print ""

    if (newVersion and not re.match(".*SNAPSHOT", newVersionNum)):
        if (versionAttrVersion != newVersionNum):
            raise ValueError("version attribute and planned version do not match", versionAttrVersion, newVersionNum)

    # print enumVersions

    if (namespaceVersion != targetNamespaceVersion):
        raise ValueError("namespace version and targetNamespace version do not match", namespaceVersion, targetNamespaceVersion)

    versionRegex = "[0-9]+\.[0-9]+\.[0-9]+"
    if (not re.match(versionRegex, versionAttrVersion)):
        raise ValueError("version attribute does not match the valid regex", versionAttrVersion, versionRegex)

    if (versionAttrVersion != idAttrVersion):
        raise ValueError("version attribute and id attribute do not match", versionAttrVersion, idAttrVersion)

    if (not versionAttrVersion.startswith(targetNamespaceVersion)):
        raise ValueError("Major version of the version attribute does not match the targetNamespace version", versionAttrVersion, targetNamespaceVersion)

    if (not versionAttrVersion in enumVersions):
        raise ValueError("Schema version is not in the list of version enumerations", versionAttrVersion, enumVersions)

    minorVer = getMinorVersion(versionAttrVersion)

    for enumVer in enumVersions:
        if (not enumVer.startswith(targetNamespaceVersion)):
            raise ValueError("Major version of the enumeration version does not match the targetNamespace version", enumVer, targetNamespaceVersion)
        minorVerOfEnum = getMinorVersion(enumVer)

        if (minorVerOfEnum > minorVer):
            raise ValueError("Minor version of enumeration version is higher than the minor version of the schema version. Should be less than or equal to the schema version", enumVer, versionAttrVersion)

def validate_xml_file(schema_path, xml_path):
    print("Validating file {} using schema {}".format(xml_path, schema_path))



# Script proper starts here
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

if len(sys.argv) != 2:
    print("ERROR - Invalid arguments")
    print_usage()
    exit(1)
else:
    xml_schema_file_path = sys.argv[1]
    xml_file_or_dir_path = sys.argv[2]

print("Using root path: {}".format(root_path))
print("xmlSchemaFilePath: {}".format(xml_schema_file_path))
print("xmlFileOrDirPath: {}".format(xml_file_or_dir_path))

if not os.path.isfile(xml_schema_file_path):
    print("ERROR - XMLSchema file {} does not exist".format(xml_schema_file_path))
    exit(1)

if os.path.isfile(xml_file_or_dir_path):
    print("xml_schema_file_path is a file")
    #process the file
    validate_xml_file(xml_schema_file_path, xml_file_or_dir_path)
elif os.path.isdir(xml_file_or_dir_path): 
    print("xml_schema_file_path is a directory")
    #process each xml file in the dir
    for the_file in os.listdir(xml_file_or_dir_path):
        file_path = os.path.join(xml_file_or_dir_path, the_file)
        validate_xml_file(xml_schema_file_path, file_path)
else:
    print("ERROR - {} is not a valid file or directory".format(xml_schema_file_path))



    
validateVersions(newVersion)

print ""
print "Done!"
exit(0)
