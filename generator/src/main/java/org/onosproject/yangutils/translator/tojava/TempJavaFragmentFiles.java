/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
package org.onosproject.yangutils.translator.tojava;

import org.onosproject.yangutils.datamodel.RpcNotificationContainer;
import org.onosproject.yangutils.datamodel.YangAugment;
import org.onosproject.yangutils.datamodel.YangAugmentableNode;
import org.onosproject.yangutils.datamodel.YangCase;
import org.onosproject.yangutils.datamodel.YangChoice;
import org.onosproject.yangutils.datamodel.YangLeaf;
import org.onosproject.yangutils.datamodel.YangLeafList;
import org.onosproject.yangutils.datamodel.YangLeavesHolder;
import org.onosproject.yangutils.datamodel.YangList;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangType;
import org.onosproject.yangutils.datamodel.javadatamodel.JavaFileInfo;
import org.onosproject.yangutils.datamodel.javadatamodel.JavaQualifiedTypeInfo;
import org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yangutils.translator.exception.TranslatorException;
import org.onosproject.yangutils.translator.tojava.javamodel.JavaLeafInfoContainer;
import org.onosproject.yangutils.translator.tojava.javamodel.YangJavaGroupingTranslator;
import org.onosproject.yangutils.translator.tojava.utils.JavaExtendsListHolder;
import org.onosproject.yangutils.utils.io.YangPluginConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.getParentNodeInGenCode;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.BUILDER_CLASS_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.BUILDER_INTERFACE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.DEFAULT_CLASS_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_INTERFACE_WITH_BUILDER;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_TYPE_CLASS;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_UNION_CLASS;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.INTERFACE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.ADD_TO_LIST_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.ADD_TO_LIST_INTERFACE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.ATTRIBUTES_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.EDIT_CONTENT_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.EQUALS_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.FILTER_CONTENT_MATCH_FOR_LEAF_LIST_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.FILTER_CONTENT_MATCH_FOR_LEAF_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.FILTER_CONTENT_MATCH_FOR_NODES_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.FROM_STRING_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.GETTER_FOR_CLASS_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.GETTER_FOR_INTERFACE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.HASH_CODE_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.LEAF_IDENTIFIER_ENUM_ATTRIBUTES_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.SETTER_FOR_CLASS_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.SETTER_FOR_INTERFACE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.TO_STRING_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.JavaAttributeInfo.getAttributeInfoForTheData;
import static org.onosproject.yangutils.translator.tojava.JavaQualifiedTypeInfoTranslator.getQualifiedInfoOfFromString;
import static org.onosproject.yangutils.translator.tojava.JavaQualifiedTypeInfoTranslator.getQualifiedTypeInfoOfCurNode;
import static org.onosproject.yangutils.translator.tojava.javamodel.AttributesJavaDataType.updateJavaFileInfo;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGeneratorUtils.generateBitsFile;
import static org.onosproject.yangutils.translator.tojava.utils.JavaCodeSnippetGen.generateEnumAttributeString;
import static org.onosproject.yangutils.translator.tojava.utils.JavaCodeSnippetGen.getJavaAttributeDefinition;
import static org.onosproject.yangutils.translator.tojava.utils.JavaCodeSnippetGen.sortImports;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGenerator.generateBuilderClassFile;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGenerator.generateBuilderInterfaceFile;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGenerator.generateDefaultClassFile;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGenerator.generateInterfaceFile;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGeneratorUtils.getFileObject;
import static org.onosproject.yangutils.translator.tojava.utils.JavaIdentifierSyntax.createPackage;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getAddToListMethodImpl;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getAddToListMethodInterface;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getBuildString;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getDefaultConstructorString;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getEqualsMethod;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getFromStringMethod;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getGetterForClass;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getGetterString;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getHashCodeMethod;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getSetterForClass;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getSetterString;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getToStringMethod;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.parseBuilderInterfaceBuildMethodString;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getImportString;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getOverRideString;
import static org.onosproject.yangutils.translator.tojava.utils.SubtreeFilteringMethodsGenerator.getSubtreeFilteringForLeaf;
import static org.onosproject.yangutils.translator.tojava.utils.SubtreeFilteringMethodsGenerator.getSubtreeFilteringForLeafList;
import static org.onosproject.yangutils.translator.tojava.utils.SubtreeFilteringMethodsGenerator.getSubtreeFilteringForNode;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorErrorType.INVALID_LEAF_HOLDER;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorErrorType.INVALID_NODE;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorErrorType.INVALID_PARENT_NODE;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorErrorType.MISSING_PARENT_NODE;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorUtils.getBeanFiles;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorUtils.getErrorMsg;
import static org.onosproject.yangutils.utils.UtilConstants.ARRAY_LIST_IMPORT;
import static org.onosproject.yangutils.utils.UtilConstants.AUGMENT_MAP_TYPE;
import static org.onosproject.yangutils.utils.UtilConstants.BIT_SET;
import static org.onosproject.yangutils.utils.UtilConstants.BUILDER;
import static org.onosproject.yangutils.utils.UtilConstants.CLOSE_CURLY_BRACKET;
import static org.onosproject.yangutils.utils.UtilConstants.DEFAULT;
import static org.onosproject.yangutils.utils.UtilConstants.DEFAULT_CAPS;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.FOUR_SPACE_INDENTATION;
import static org.onosproject.yangutils.utils.UtilConstants.INTERFACE;
import static org.onosproject.yangutils.utils.UtilConstants.INVOCATION_TARGET_EXCEPTION_IMPORT;
import static org.onosproject.yangutils.utils.UtilConstants.JAVA_UTIL_PKG;
import static org.onosproject.yangutils.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yangutils.utils.UtilConstants.OPERATION_TYPE_ATTRIBUTE;
import static org.onosproject.yangutils.utils.UtilConstants.OPERATION_TYPE_CLASS;
import static org.onosproject.yangutils.utils.UtilConstants.OP_PARAM;
import static org.onosproject.yangutils.utils.UtilConstants.PERIOD;
import static org.onosproject.yangutils.utils.UtilConstants.PRIVATE;
import static org.onosproject.yangutils.utils.UtilConstants.PROTECTED;
import static org.onosproject.yangutils.utils.UtilConstants.SELECT_LEAF;
import static org.onosproject.yangutils.utils.UtilConstants.SERVICE;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH;
import static org.onosproject.yangutils.utils.UtilConstants.VALUE_LEAF;
import static org.onosproject.yangutils.utils.UtilConstants.YANG;
import static org.onosproject.yangutils.utils.UtilConstants.YANG_AUGMENTED_INFO_MAP;
import static org.onosproject.yangutils.utils.io.impl.FileSystemUtil.closeFile;
import static org.onosproject.yangutils.utils.io.impl.FileSystemUtil.readAppendFile;
import static org.onosproject.yangutils.utils.io.impl.JavaDocGen.JavaDocType.ADD_TO_LIST;
import static org.onosproject.yangutils.utils.io.impl.JavaDocGen.JavaDocType.GETTER_METHOD;
import static org.onosproject.yangutils.utils.io.impl.JavaDocGen.getJavaDoc;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getAbsolutePackagePath;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getCapitalCase;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getPackageDirPathFromJavaJPackage;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.insertDataIntoJavaFile;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.mergeJavaFiles;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.validateLineLength;

/**
 * Represents implementation of java code fragments temporary implementations.
 * Manages the common temp file required for Java file(s) generated.
 */
public class TempJavaFragmentFiles {

    /**
     * File type extension for java classes.
     */
    private static final String JAVA_FILE_EXTENSION = ".java";

    /**
     * File type extension for temporary classes.
     */
    private static final String TEMP_FILE_EXTENSION = ".tmp";

    /**
     * Folder suffix for temporary files folder.
     */
    private static final String TEMP_FOLDER_NAME_SUFFIX = "-Temp";

    /**
     * File name for getter method.
     */
    private static final String GETTER_METHOD_FILE_NAME = "GetterMethod";

    /**
     * File name for setter method.
     */
    private static final String SETTER_METHOD_FILE_NAME = "SetterMethod";

    /**
     * File name for getter method implementation.
     */
    private static final String GETTER_METHOD_IMPL_FILE_NAME =
            "GetterMethodImpl";

    /**
     * File name for setter method implementation.
     */
    private static final String SETTER_METHOD_IMPL_FILE_NAME =
            "SetterMethodImpl";

    /**
     * File name for attributes.
     */
    private static final String ATTRIBUTE_FILE_NAME = "Attributes";

    /**
     * File name for to string method.
     */
    private static final String TO_STRING_METHOD_FILE_NAME = "ToString";

    /**
     * File name for hash code method.
     */
    private static final String HASH_CODE_METHOD_FILE_NAME = "HashCode";

    /**
     * File name for equals method.
     */
    private static final String EQUALS_METHOD_FILE_NAME = "Equals";

    /**
     * File name for from string method.
     */
    private static final String FROM_STRING_METHOD_FILE_NAME = "FromString";

    /**
     * File name for from add to list interface method.
     */
    private static final String ADD_TO_LIST_INTERFACE_METHOD_FILE_NAME =
            "addToList";

    /**
     * File name for from add to list impl method.
     */
    private static final String ADD_TO_LIST_IMPL_METHOD_FILE_NAME =
            "addToListImpl";

    /**
     * File name for from leaf identifier attributes.
     */
    private static final String LEAF_IDENTIFIER_ATTRIBUTES_FILE_NAME =
            "leafIdentifierAtr";

    /**
     * File name for is filter content leaf match.
     */
    private static final String FILTER_CONTENT_MATCH_LEAF_FILE_NAME =
            "isFilterContentMatchLeafMask";

    /**
     * File name for is filter content leaf-list match.
     */
    private static final String FILTER_CONTENT_MATCH_LEAF_LIST_FILE_NAME =
            "isFilterContentMatchLeafListMask";

    /**
     * File name for is filter content node match.
     */
    private static final String FILTER_CONTENT_MATCH_NODE_FILE_NAME =
            "isFilterContentMatchNodeMask";

    /**
     * File name for edit content file.
     */
    private static final String EDIT_CONTENT_FILE_NAME = "editContentFile";

    /**
     * File name for interface java file name suffix.
     */
    private static final String INTERFACE_FILE_NAME_SUFFIX = EMPTY_STRING;

    /**
     * File name for builder interface file name suffix.
     */
    private static final String BUILDER_INTERFACE_FILE_NAME_SUFFIX =
            BUILDER + INTERFACE;

    /**
     * File name for builder class file name suffix.
     */
    private static final String BUILDER_CLASS_FILE_NAME_SUFFIX = BUILDER;

    /**
     *
     */
    private boolean isBinary;

    /**
     * Information about the java files being generated.
     */
    private JavaFileInfoTranslator javaFileInfo;

    /**
     * Imported class info.
     */
    private JavaImportData javaImportData;

    /**
     * The variable which guides the types of temporary files generated using
     * the temporary generated file types mask.
     */
    private int tempFilesFlagSet;

    /**
     * Absolute path where the target java file needs to be generated.
     */
    private String absoluteDirPath;

    /**
     * Contains all the interface(s)/class name which will be extended by
     * generated files.
     */
    private JavaExtendsListHolder javaExtendsListHolder;

    /**
     * Java file handle for interface file.
     */
    private File interfaceJavaFileHandle;

    /**
     * Java file handle for builder interface file.
     */
    private File builderInterfaceJavaFileHandle;

    /**
     * Java file handle for builder class file.
     */
    private File builderClassJavaFileHandle;

    /**
     * Java file handle for impl class file.
     */
    private File implClassJavaFileHandle;

    /**
     * Temporary file handle for attribute.
     */
    private File attributesTempFileHandle;

    /**
     * Temporary file handle for getter of interface.
     */
    private File getterInterfaceTempFileHandle;

    /**
     * Temporary file handle for setter of interface.
     */
    private File setterInterfaceTempFileHandle;

    /**
     * Temporary file handle for getter of class.
     */
    private File getterImplTempFileHandle;

    /**
     * Temporary file handle for setter of class.
     */
    private File setterImplTempFileHandle;

    /**
     * Temporary file handle for hash code method of class.
     */
    private File hashCodeImplTempFileHandle;

    /**
     * Temporary file handle for equals method of class.
     */
    private File equalsImplTempFileHandle;

    /**
     * Temporary file handle for to string method of class.
     */
    private File toStringImplTempFileHandle;

    /**
     * Temporary file handle for from string method of class.
     */
    private File fromStringImplTempFileHandle;

    /**
     * Temporary file handle for add to list interface method of class.
     */
    private File addToListInterfaceTempFileHandle;

    /**
     * Temporary file handle for add to list impl method of class.
     */
    private File addToListImplTempFileHandle;

    /**
     * Temporary file handle for leaf id attributes of enum.
     */
    private File leafIdAttributeTempFileHandle;

    /**
     * Temporary file handle for is content match method for leaf-list.
     */
    private File getSubtreeFilteringForListTempFileHandle;

    /**
     * Temporary file handle for is content match method for node.
     */
    private File getSubtreeFilteringForChildNodeTempFileHandle;

    /**
     * Temporary file handle for is content match method for leaf.
     */
    private File subtreeFilteringForLeafTempFileHandle;

    /**
     * Temporary file handle for edit content file.
     */
    private File editContentTempFileHandle;

    /**
     * Leaf count.
     */
    private int leafCount;

    /**
     * If current node is root node.
     */
    private boolean rootNode;

    /**
     * Is attribute added.
     */
    private boolean isAttributePresent;

    /**
     * Creates an instance of temp JAVA fragment files.
     */
    TempJavaFragmentFiles() {
    }

    /**
     * Creates an instance of temporary java code fragment.
     *
     * @param fileInfo generated java file information
     * @throws IOException when fails to create new file handle
     */
    protected TempJavaFragmentFiles(JavaFileInfoTranslator fileInfo)
            throws IOException {
        javaExtendsListHolder = new JavaExtendsListHolder();
        javaImportData = new JavaImportData();
        javaFileInfo = fileInfo;
        absoluteDirPath = getAbsolutePackagePath(fileInfo.getBaseCodeGenPath(),
                                                 fileInfo.getPackageFilePath());
        /*
         * Initialize getter when generation file type matches to interface
         * mask.
         */
        if (javaFlagSet(INTERFACE_MASK)) {
            addGeneratedTempFile(GETTER_FOR_INTERFACE_MASK |
                                         ADD_TO_LIST_INTERFACE_MASK |
                                         LEAF_IDENTIFIER_ENUM_ATTRIBUTES_MASK);
        }
        /*
         * Initialize getter and setter when generation file type matches to
         * builder interface mask.
         */
        if (javaFlagSet(BUILDER_INTERFACE_MASK)) {
            addGeneratedTempFile(GETTER_FOR_INTERFACE_MASK |
                                         SETTER_FOR_INTERFACE_MASK);
        }
        /*
         * Initialize getterImpl, setterImpl and attributes when generation file
         * type matches to builder class mask.
         */
        if (javaFlagSet(BUILDER_CLASS_MASK)) {
            addGeneratedTempFile(ATTRIBUTES_MASK | GETTER_FOR_CLASS_MASK |
                                         SETTER_FOR_CLASS_MASK);
        }
        /*
         * Initialize getterImpl, attributes, constructor, hash code, equals and
         * to strings when generation file type matches to impl class mask.
         */
        if (javaFlagSet(DEFAULT_CLASS_MASK)) {
            addGeneratedTempFile(
                    ATTRIBUTES_MASK | GETTER_FOR_CLASS_MASK |
                            HASH_CODE_IMPL_MASK | EQUALS_IMPL_MASK |
                            TO_STRING_IMPL_MASK | ADD_TO_LIST_IMPL_MASK |
                            FILTER_CONTENT_MATCH_FOR_LEAF_LIST_MASK |
                            FILTER_CONTENT_MATCH_FOR_LEAF_MASK |
                            FILTER_CONTENT_MATCH_FOR_NODES_MASK);
        }
        /*
         * Initialize temp files to generate type class.
         */
        if (javaFlagSet(GENERATE_TYPE_CLASS)) {
            addGeneratedTempFile(ATTRIBUTES_MASK | GETTER_FOR_CLASS_MASK |
                                         HASH_CODE_IMPL_MASK | EQUALS_IMPL_MASK |
                                         FROM_STRING_IMPL_MASK);

            if (getGeneratedJavaFiles() != GENERATE_UNION_CLASS) {
                addGeneratedTempFile(TO_STRING_IMPL_MASK);
            }
        }

        //Set temporary file handles
        if (tempFlagSet(ATTRIBUTES_MASK)) {
            attributesTempFileHandle =
                    getTemporaryFileHandle(ATTRIBUTE_FILE_NAME);
        }
        if (tempFlagSet(GETTER_FOR_INTERFACE_MASK)) {
            getterInterfaceTempFileHandle =
                    getTemporaryFileHandle(GETTER_METHOD_FILE_NAME);
        }
        if (tempFlagSet(SETTER_FOR_INTERFACE_MASK)) {
            setterInterfaceTempFileHandle =
                    getTemporaryFileHandle(SETTER_METHOD_FILE_NAME);
        }
        if (tempFlagSet(GETTER_FOR_CLASS_MASK)) {
            getterImplTempFileHandle =
                    getTemporaryFileHandle(GETTER_METHOD_IMPL_FILE_NAME);
        }
        if (tempFlagSet(SETTER_FOR_CLASS_MASK)) {
            setterImplTempFileHandle =
                    getTemporaryFileHandle(SETTER_METHOD_IMPL_FILE_NAME);
        }
        if (tempFlagSet(HASH_CODE_IMPL_MASK)) {
            hashCodeImplTempFileHandle =
                    getTemporaryFileHandle(HASH_CODE_METHOD_FILE_NAME);
        }
        if (tempFlagSet(EQUALS_IMPL_MASK)) {
            equalsImplTempFileHandle =
                    getTemporaryFileHandle(EQUALS_METHOD_FILE_NAME);
        }
        if (tempFlagSet(TO_STRING_IMPL_MASK)) {
            toStringImplTempFileHandle =
                    getTemporaryFileHandle(TO_STRING_METHOD_FILE_NAME);
        }
        if (tempFlagSet(FROM_STRING_IMPL_MASK)) {
            fromStringImplTempFileHandle =
                    getTemporaryFileHandle(FROM_STRING_METHOD_FILE_NAME);
        }
        if (tempFlagSet(ADD_TO_LIST_INTERFACE_MASK)) {
            addToListInterfaceTempFileHandle =
                    getTemporaryFileHandle(ADD_TO_LIST_INTERFACE_METHOD_FILE_NAME);
        }
        if (tempFlagSet(ADD_TO_LIST_IMPL_MASK)) {
            addToListImplTempFileHandle =
                    getTemporaryFileHandle(ADD_TO_LIST_IMPL_METHOD_FILE_NAME);
        }
        if (tempFlagSet(LEAF_IDENTIFIER_ENUM_ATTRIBUTES_MASK)) {
            leafIdAttributeTempFileHandle =
                    getTemporaryFileHandle(LEAF_IDENTIFIER_ATTRIBUTES_FILE_NAME);
        }
        if (tempFlagSet(FILTER_CONTENT_MATCH_FOR_LEAF_MASK)) {
            subtreeFilteringForLeafTempFileHandle =
                    getTemporaryFileHandle(FILTER_CONTENT_MATCH_LEAF_FILE_NAME);
        }
        if (tempFlagSet(FILTER_CONTENT_MATCH_FOR_LEAF_LIST_MASK)) {
            getSubtreeFilteringForListTempFileHandle =
                    getTemporaryFileHandle(FILTER_CONTENT_MATCH_LEAF_LIST_FILE_NAME);
        }
        if (tempFlagSet(FILTER_CONTENT_MATCH_FOR_NODES_MASK)) {
            getSubtreeFilteringForChildNodeTempFileHandle =
                    getTemporaryFileHandle(FILTER_CONTENT_MATCH_NODE_FILE_NAME);
        }
        if (tempFlagSet(EDIT_CONTENT_MASK)) {
            editContentTempFileHandle =
                    getTemporaryFileHandle(EDIT_CONTENT_FILE_NAME);
        }
    }

    /**
     * Adds current node info as and attribute to the parent generated file.
     *
     * @param curNode current node
     * @param isList  is list construct
     * @param config  plugin configurations
     * @throws IOException IO operation exception
     */
    protected static void addCurNodeInfoInParentTempFile(YangNode curNode,
                                                         boolean isList,
                                                         YangPluginConfig config)
            throws IOException {
        YangNode parent = getParentNodeInGenCode(curNode);
        if (!(parent instanceof JavaCodeGenerator)) {
            throw new TranslatorException(getErrorMsg(MISSING_PARENT_NODE,
                                                      curNode));
        }
        if (parent instanceof YangJavaGroupingTranslator) {
            /*
             * In case of grouping, there is no need to add the information, it
             * will be taken care in uses.
             */
            return;
        }
        addCurNodeInfoInParentTempFile(curNode, isList, config, parent);
    }

    /**
     * Adds current node info as and attribute to a specified parent generated
     * file. In case of grouping parent will be referred grouping node or
     * referred node in grouping.
     *
     * @param curNode current node
     * @param isList  is list construct
     * @param config  plugin configurations
     * @param parent  parent node
     * @throws IOException IO operation exception
     */
    protected static void addCurNodeInfoInParentTempFile(
            YangNode curNode, boolean isList, YangPluginConfig config,
            YangNode parent)
            throws IOException {
        TempJavaBeanFragmentFiles tempFiles =
                getBeanFiles((JavaCodeGeneratorInfo) parent);
        JavaAttributeInfo attr =
                getCurNodeAsAttributeInTarget(curNode, parent, isList,
                                              tempFiles);
        tempFiles.addJavaSnippetInfoToApplicableTempFiles(attr, config);
    }

    /**
     * Creates an attribute info object corresponding to a data model node
     * and return it.
     *
     * @param curNode    current node
     * @param targetNode target node
     * @param listNode   flag indicating if a node is a list node
     * @param tempFiles  temp java fragment files
     * @return java attribute info
     */
    public static JavaAttributeInfo
    getCurNodeAsAttributeInTarget(YangNode curNode, YangNode targetNode,
                                  boolean listNode,
                                  TempJavaFragmentFiles tempFiles) {
        JavaFileInfoTranslator translator =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();
        String curNodeName = translator.getJavaName();
        if (curNodeName == null) {
            updateJavaFileInfo(curNode, null);
            curNodeName = translator.getJavaName();
        }
        /*
         * Get the import info corresponding to the attribute for import in
         * generated java files or qualified access.
         */
        JavaQualifiedTypeInfoTranslator typeInfo =
                getQualifiedTypeInfoOfCurNode(curNode,
                                              getCapitalCase(curNodeName));
        if (!(targetNode instanceof TempJavaCodeFragmentFilesContainer)) {
            throw new TranslatorException(getErrorMsg(INVALID_PARENT_NODE,
                                                      curNode));
        }
        JavaImportData parentImportData = tempFiles.getJavaImportData();
        JavaFileInfoTranslator fileInfo =
                ((JavaFileInfoContainer) targetNode).getJavaFileInfo();

        boolean qualified;
        if (tempFiles instanceof TempJavaServiceFragmentFiles &&
                typeInfo.getClassInfo().contentEquals(SERVICE) ||
                typeInfo.getClassInfo()
                        .contentEquals(getCapitalCase(fileInfo.getJavaName() +
                                                              SERVICE))) {
            qualified = true;
        } else {
            String className;
            if (tempFiles instanceof TempJavaServiceFragmentFiles) {
                className = getCapitalCase(fileInfo.getJavaName()) + SERVICE;
            } else {
                className = getCapitalCase(fileInfo.getJavaName());
            }
            qualified = parentImportData.addImportInfo(typeInfo, className,
                                                       fileInfo.getPackage());
        }
        boolean collectionSet = false;
        if (curNode instanceof YangList) {
            YangList yangList = (YangList) curNode;
            if (yangList.getCompilerAnnotation() != null &&
                    yangList.getCompilerAnnotation()
                            .getYangAppDataStructure() != null) {
                switch (yangList.getCompilerAnnotation()
                        .getYangAppDataStructure().getDataStructure()) {
                    case QUEUE: {
                        parentImportData.setQueueToImport(true);
                        collectionSet = true;
                        break;
                    }
                    case SET: {
                        parentImportData.setSetToImport(true);
                        collectionSet = true;
                        break;
                    }
                    default: {
                        // TODO : to be implemented
                    }
                }
            }
        }
        if (listNode && !collectionSet) {
            parentImportData.setIfListImported(true);
        }
        if (curNode instanceof YangList) {
            return getAttributeInfoForTheData(typeInfo, curNodeName,
                                              null, qualified, listNode,
                                              ((YangList) curNode)
                                                      .getCompilerAnnotation());
        }
        return getAttributeInfoForTheData(typeInfo, curNodeName, null,
                                          qualified, listNode);
    }

    /**
     * Returns java attribute for leaf.
     *
     * @param tempFiles temporary generated file
     * @param container JAVA leaf info container
     * @param config    plugin configurations
     * @param leafList  flag indicating if it's leaf list
     * @return java attribute info
     */
    private static JavaAttributeInfo
    getJavaAttributeOfLeaf(TempJavaFragmentFiles tempFiles,
                           JavaLeafInfoContainer container,
                           YangPluginConfig config, boolean leafList) {
        if (leafList) {
            tempFiles.getJavaImportData().setIfListImported(true);
            return getAttributeOfLeafInfoContainer(tempFiles, container, config,
                                                   true);
        }
        return getAttributeOfLeafInfoContainer(tempFiles, container, config,
                                               false);
    }

    /**
     * Returns java attribute for leaf container.
     *
     * @param tempFiles     temporary generated file
     * @param container     JAVA leaf info container
     * @param config        plugin configurations
     * @param listAttribute flag indicating if list attribute
     * @return JAVA attribute information
     */
    private static JavaAttributeInfo
    getAttributeOfLeafInfoContainer(TempJavaFragmentFiles tempFiles,
                                    JavaLeafInfoContainer container,
                                    YangPluginConfig config,
                                    boolean listAttribute) {
        container.setConflictResolveConfig(config.getConflictResolver());
        container.updateJavaQualifiedInfo();
        addImportForLeafInfo(tempFiles, container);
        JavaAttributeInfo attr = getAttributeInfoForTheData(
                container.getJavaQualifiedInfo(),
                container.getJavaName(config.getConflictResolver()),
                container.getDataType(),
                tempFiles.getIsQualifiedAccessOrAddToImportList(
                        container.getJavaQualifiedInfo()), listAttribute);
        if (container.getDataType().getDataType() == YangDataTypes.BITS) {
            addBitsHandler(attr, container.getDataType(), tempFiles);
        }
        return attr;
    }

    /**
     * Adds bits handler attribute for bits to string method.
     *
     * @param attr      attribute
     * @param type      type
     * @param tempFiles temp fragment file
     */
    static void addBitsHandler(JavaAttributeInfo attr, YangType type,
                               TempJavaFragmentFiles tempFiles) {
        try {
            generateBitsFile(attr, type, tempFiles.getJavaFileInfo(), tempFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds attribute types import to leaf info container's parent.
     *
     * @param tempFiles temp java file
     * @param container leaf info container
     */
    private static void addImportForLeafInfo(TempJavaFragmentFiles tempFiles,
                                             JavaLeafInfoContainer container) {
        String containedInCls = getCapitalCase(tempFiles.getJavaFileInfo()
                                                       .getJavaName());
        String containedInPkg = tempFiles.getJavaFileInfo().getPackage();
        JavaQualifiedTypeInfoTranslator info;
        if (container.getDataType().getDataType() == YangDataTypes.BINARY) {
            tempFiles.setBinary(true);
        } else if (container.getDataType().getDataType() == YangDataTypes.BITS) {
            //Add bitset import for type and leaf value flags.
            info = new JavaQualifiedTypeInfoTranslator();
            info.setClassInfo(BIT_SET);
            info.setPkgInfo(JAVA_UTIL_PKG);
            tempFiles.getJavaImportData().addImportInfo(info, containedInCls,
                                                        containedInPkg);
        }
        tempFiles.getJavaImportData().addImportInfo(
                (JavaQualifiedTypeInfoTranslator) container
                        .getJavaQualifiedInfo(), containedInCls, containedInPkg);

    }

    /**
     * Sets absolute path where the file needs to be generated.
     *
     * @param absoluteDirPath absolute path where the file needs to be
     *                        generated
     */
    protected void setAbsoluteDirPath(String absoluteDirPath) {
        this.absoluteDirPath = absoluteDirPath;
    }

    /**
     * Returns the generated java file information.
     *
     * @return generated java file information
     */
    protected JavaFileInfoTranslator getJavaFileInfo() {
        return javaFileInfo;
    }

    /**
     * Sets the generated java file information.
     *
     * @param javaFileInfo generated java file information
     */
    protected void setJavaFileInfo(JavaFileInfoTranslator javaFileInfo) {
        this.javaFileInfo = javaFileInfo;
    }

    /**
     * Returns the flag-set for generated temp files.
     *
     * @return flag-set
     */
    protected int getGeneratedTempFiles() {
        return tempFilesFlagSet;
    }

    /**
     * Adds to the flag-set for generated temp files.
     *
     * @param flags generated temp files flag-set
     */
    protected void addGeneratedTempFile(int flags) {
        tempFilesFlagSet |= flags;
    }

    /**
     * Returns the generated Java files.
     *
     * @return generated Java files
     */
    protected int getGeneratedJavaFiles() {
        return javaFileInfo.getGeneratedFileTypes();
    }

    /**
     * Retrieves the mapped Java class name.
     *
     * @return mapped Java class name
     */
    protected String getGeneratedJavaClassName() {
        return getCapitalCase(javaFileInfo.getJavaName());
    }

    /**
     * Retrieves the import data for the generated Java file.
     *
     * @return import data for the generated Java file
     */
    public JavaImportData getJavaImportData() {
        return javaImportData;
    }

    /**
     * Sets import data for the generated Java file.
     *
     * @param data import data for the generated Java file
     */
    protected void setJavaImportData(JavaImportData data) {
        javaImportData = data;
    }

    /**
     * Retrieves the status of any attributes added.
     *
     * @return status of any attributes added
     */
    protected boolean isAttributePresent() {
        return isAttributePresent;
    }

    /**
     * Returns getter methods's temporary file handle.
     *
     * @return temporary file handle
     */
    public File getGetterInterfaceTempFileHandle() {
        return getterInterfaceTempFileHandle;
    }

    /**
     * Returns setter method's temporary file handle.
     *
     * @return temporary file handle
     */
    public File getSetterInterfaceTempFileHandle() {
        return setterInterfaceTempFileHandle;
    }

    /**
     * Returns setter method's impl's temporary file handle.
     *
     * @return temporary file handle
     */
    public File getSetterImplTempFileHandle() {
        return setterImplTempFileHandle;
    }

    /**
     * Returns from string method's temporary file handle.
     *
     * @return from string method's temporary file handle
     */
    public File getFromStringImplTempFileHandle() {
        return fromStringImplTempFileHandle;
    }

    /**
     * Returns attribute's temporary file handle.
     *
     * @return temporary file handle
     */
    public File getAttributesTempFileHandle() {
        return attributesTempFileHandle;
    }

    /**
     * Returns getter method's impl's temporary file handle.
     *
     * @return temporary file handle
     */
    public File getGetterImplTempFileHandle() {
        return getterImplTempFileHandle;
    }

    /**
     * Returns hash code method's temporary file handle.
     *
     * @return temporary file handle
     */
    public File getHashCodeImplTempFileHandle() {
        return hashCodeImplTempFileHandle;
    }

    /**
     * Returns equals method's temporary file handle.
     *
     * @return temporary file handle
     */
    public File getEqualsImplTempFileHandle() {
        return equalsImplTempFileHandle;
    }

    /**
     * Returns to string method's temporary file handle.
     *
     * @return temporary file handle
     */
    public File getToStringImplTempFileHandle() {
        return toStringImplTempFileHandle;
    }

    /**
     * Returns java extends list holder.
     *
     * @return java extends list holder
     */
    public JavaExtendsListHolder getJavaExtendsListHolder() {
        return javaExtendsListHolder;
    }

    /**
     * Sets java extends list holder.
     *
     * @param holder java extends list holder
     */
    protected void setJavaExtendsListHolder(
            JavaExtendsListHolder holder) {
        javaExtendsListHolder = holder;
    }

    /**
     * Adds is filter content match for leaf.
     *
     * @param attr java attribute
     * @throws IOException when fails to do IO operations
     */
    private void addSubTreeFilteringForLeaf(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(subtreeFilteringForLeafTempFileHandle,
                     getSubtreeFilteringForLeaf(attr, attr.getAttributeType()) +
                             NEW_LINE);
    }

    /**
     * Adds is filter content match for leaf-list.
     *
     * @param attr java attribute
     * @throws IOException when fails to do IO operations
     */
    private void addSubtreeFilteringForLeafList(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(getSubtreeFilteringForListTempFileHandle,
                     getSubtreeFilteringForLeafList(attr) + NEW_LINE);
    }

    /**
     * Adds is filter content match for nodes.
     *
     * @param attr java attribute
     * @throws IOException when fails to do IO operations
     */
    private void addSubtreeFilteringForChildNode(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(getSubtreeFilteringForChildNodeTempFileHandle,
                     getSubtreeFilteringForNode(attr) + NEW_LINE);
    }

    /**
     * Adds attribute for class.
     *
     * @param attr attribute info
     * @throws IOException when fails to append to temporary file
     */
    private void addAttribute(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(attributesTempFileHandle, parseAttribute(attr));
    }

    /**
     * Adds getter for interface.
     *
     * @param attr attribute info
     * @throws IOException when fails to append to temporary file
     */
    private void addGetterForInterface(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(getterInterfaceTempFileHandle,
                     getGetterString(attr, getGeneratedJavaFiles()) +
                             NEW_LINE);
    }

    /**
     * Adds setter for interface.
     *
     * @param attr attribute info
     * @throws IOException when fails to append to temporary file
     */
    private void addSetterForInterface(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(setterInterfaceTempFileHandle,
                     getSetterString(attr, getGeneratedJavaClassName(),
                                     getGeneratedJavaFiles()) +
                             NEW_LINE);
    }

    /**
     * Adds setter's implementation for class.
     *
     * @param attr attribute info
     * @throws IOException when fails to append to temporary file
     */
    private void addSetterImpl(JavaAttributeInfo attr)
            throws IOException {
        String setter = getSetterForClass(attr, getGeneratedJavaClassName(),
                                          getGeneratedJavaFiles());
        appendToFile(setterImplTempFileHandle, getOverRideString() + setter);
    }

    /**
     * Adds getter method's impl for class.
     *
     * @param attr attribute info
     * @throws IOException when fails to append to temporary file
     */
    protected void addGetterImpl(JavaAttributeInfo attr)
            throws IOException {
        String getter = getGetterForClass(attr, getGeneratedJavaFiles());
        if (javaFlagSet(BUILDER_CLASS_MASK)) {
            appendToFile(getterImplTempFileHandle, getOverRideString() +
                    getter);
        } else {
            String appDataStructure = null;
            if (attr.getCompilerAnnotation() != null) {
                appDataStructure = attr.getCompilerAnnotation()
                        .getYangAppDataStructure().getDataStructure().name();
            }
            appendToFile(getterImplTempFileHandle,
                         getJavaDoc(GETTER_METHOD, attr.getAttributeName(),
                                    false, appDataStructure) + getter);
        }
    }

    /**
     * Adds add to list interface method.
     *
     * @param attr attribute
     * @throws IOException when fails to do IO operations
     */
    private void addAddToListInterface(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(addToListInterfaceTempFileHandle,
                     getJavaDoc(ADD_TO_LIST, attr.getAttributeName(), false,
                                null) + getAddToListMethodInterface(
                             attr, getGeneratedJavaClassName()) + NEW_LINE);
    }

    /**
     * Adds add to list interface method.
     *
     * @param attr attribute
     * @throws IOException when fails to do IO operations
     */
    private void addAddToListImpl(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(addToListImplTempFileHandle,
                     getAddToListMethodImpl(attr, getGeneratedJavaClassName()
                     ) + NEW_LINE);
    }

    /**
     * Adds leaf identifier enum attributes.
     *
     * @param attr  attribute
     * @param value value
     * @throws IOException when fails to do IO operations
     */
    private void addLeafIdAttributes(JavaAttributeInfo attr, int value)
            throws IOException {
        appendToFile(leafIdAttributeTempFileHandle, FOUR_SPACE_INDENTATION +
                generateEnumAttributeString(attr.getAttributeName(), value));
    }

    /**
     * Adds build method for interface.
     *
     * @return build method for interface
     * @throws IOException when fails to append to temporary file
     */
    protected String addBuildMethodForInterface()
            throws IOException {
        return parseBuilderInterfaceBuildMethodString(
                getGeneratedJavaClassName());
    }

    /**
     * Adds build method's implementation for class.
     *
     * @return build method implementation for class
     * @throws IOException when fails to append to temporary file
     */
    protected String addBuildMethodImpl()
            throws IOException {
        return getBuildString(getGeneratedJavaClassName(), rootNode) + NEW_LINE;
    }

    /**
     * Adds default constructor for class.
     *
     * @param modifier modifier for constructor
     * @param toAppend string which need to be appended with the class name
     * @param suffix   is value need to be appended as suffix
     * @return default constructor for class
     * @throws IOException when fails to append to file
     */
    protected String addDefaultConstructor(String modifier, String toAppend,
                                           boolean suffix)
            throws IOException {
        StringBuilder name = new StringBuilder();
        name.append(getGeneratedJavaClassName());
        if (rootNode && !toAppend.equals(BUILDER)) {
            name.append(OP_PARAM);
            return getDefaultConstructorString(name.toString(), modifier);
        }
        if (suffix) {
            name.append(toAppend);
            return getDefaultConstructorString(name.toString(), modifier);
        }
        StringBuilder appended = new StringBuilder();
        if (toAppend.equals(DEFAULT)) {
            appended.append(getCapitalCase(toAppend));
        } else {
            appended.append(toAppend);
        }
        return NEW_LINE + getDefaultConstructorString(appended.append(
                name).toString(), modifier);
        // TODO getDefaultConstructorString to handle new line.
    }

    /**
     * Adds hash code method for class.
     *
     * @param attr attribute info
     * @throws IOException when fails to append to temporary file
     */
    private void addHashCodeMethod(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(hashCodeImplTempFileHandle,
                     getHashCodeMethod(attr) + NEW_LINE);
    }

    /**
     * Adds equals method for class.
     *
     * @param attr attribute info
     * @throws IOException when fails to append to temporary file
     */
    private void addEqualsMethod(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(equalsImplTempFileHandle,
                     getEqualsMethod(attr) + NEW_LINE);
    }

    /**
     * Adds ToString method for class.
     *
     * @param attr attribute info
     * @throws IOException when fails to append to temporary file
     */
    private void addToStringMethod(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(toStringImplTempFileHandle,
                     getToStringMethod(attr) + NEW_LINE);
    }

    /**
     * Adds from string method for union class.
     *
     * @param attr           type attribute info
     * @param fromStringAttr from string attribute info
     * @throws IOException when fails to append to temporary file
     */
    void addFromStringMethod(JavaAttributeInfo attr,
                             JavaAttributeInfo fromStringAttr)
            throws IOException {
        appendToFile(fromStringImplTempFileHandle,
                     getFromStringMethod(attr, fromStringAttr) + NEW_LINE);
    }

    /**
     * Returns a temporary file handle for the specific file type.
     *
     * @param fileName file name
     * @return temporary file handle
     * @throws IOException when fails to create new file handle
     */
    protected File getTemporaryFileHandle(String fileName)
            throws IOException {
        String path = getTempDirPath(absoluteDirPath);
        File dir = new File(path);
        boolean isCreated;
        if (!dir.exists()) {
            isCreated = dir.mkdirs();
            if (!isCreated) {
                throw new IOException("failed to create temporary directory " +
                                              "for " + fileName);
            }
        }
        File file = new File(path + fileName + TEMP_FILE_EXTENSION);
        if (!file.exists()) {
            isCreated = file.createNewFile();
            if (!isCreated) {
                throw new IOException("failed to create temporary file for " +
                                              fileName);
            }
        } else {
            throw new IOException(fileName + " is reused due to YANG naming. " +
                                          "probably your previous build " +
                                          "would have failed");
        }
        return file;
    }

    /**
     * Returns a temporary file handle for the specific file type.
     *
     * @param fileName file name
     * @return temporary file handle
     * @throws IOException when fails to create new file handle
     */
    public File getJavaFileHandle(String fileName)
            throws IOException {
        return getFileObject(getDirPath(), fileName, JAVA_FILE_EXTENSION,
                             javaFileInfo);
    }

    /**
     * Returns data from the temporary files.
     *
     * @param file         temporary file handle
     * @param absolutePath absolute path
     * @return stored data from temporary files
     * @throws IOException when failed to get data from the given file
     */
    public String getTemporaryDataFromFileHandle(File file, String absolutePath)
            throws IOException {
        String path = getTempDirPath(absolutePath);
        if (new File(path + file.getName()).exists()) {
            return readAppendFile(path + file.getName(), EMPTY_STRING);
        }
        throw new IOException("Unable to get data from the given " +
                                      file.getName() + " file for " +
                                      getGeneratedJavaClassName() + PERIOD);
    }

    /**
     * Returns temporary directory path.
     *
     * @param path absolute path
     * @return directory path
     */
    private String getTempDirPath(String path) {
        return getPackageDirPathFromJavaJPackage(path) + SLASH +
                getGeneratedJavaClassName() + TEMP_FOLDER_NAME_SUFFIX + SLASH;
    }

    /**
     * Parses attribute to get the attribute string.
     *
     * @param attr attribute info
     * @return attribute string
     */
    protected String parseAttribute(JavaAttributeInfo attr) {
        /*
         * TODO: check if this utility needs to be called or move to the caller
         */
        String attrName = attr.getAttributeName();
        String attrAccessType = PRIVATE;
        if ((getGeneratedJavaFiles() & GENERATE_INTERFACE_WITH_BUILDER) != 0) {
            attrAccessType = PROTECTED;
        }
        String pkg = null;
        if (attr.isQualifiedName()) {
            pkg = attr.getImportInfo().getPkgInfo();
        }
        return getJavaAttributeDefinition(
                pkg, attr.getImportInfo().getClassInfo(), attrName,
                attr.isListAttr(), attrAccessType, attr.getCompilerAnnotation());
    }

    /**
     * Appends content to temporary file.
     *
     * @param file temporary file
     * @param data data to be appended
     * @throws IOException when fails to append to file
     */
    protected void appendToFile(File file, String data)
            throws IOException {
        try {
            insertDataIntoJavaFile(file, data);
        } catch (IOException ex) {
            throw new IOException("failed to write in temp file.");
        }
    }

    /**
     * Adds parent's info to current node import list.
     *
     * @param curNode current node
     * @param config  plugin configurations
     */
    protected void addParentInfoInCurNodeTempFile(YangNode curNode,
                                                  YangPluginConfig config) {
        JavaQualifiedTypeInfoTranslator caseImportInfo =
                new JavaQualifiedTypeInfoTranslator();
        YangNode parent = getParentNodeInGenCode(curNode);
        if (curNode instanceof YangCase && parent instanceof YangAugment) {
            return;
        }
        if (!(parent instanceof JavaCodeGenerator)) {
            throw new TranslatorException(getErrorMsg(INVALID_PARENT_NODE, curNode));
        }
        if (!(curNode instanceof JavaFileInfoContainer)) {
            throw new TranslatorException(getErrorMsg(INVALID_NODE, curNode));
        }
        caseImportInfo.setClassInfo(
                getCapitalCase(getCamelCase(parent.getName(),
                                            config.getConflictResolver())));
        caseImportInfo.setPkgInfo(((JavaFileInfoContainer) parent).getJavaFileInfo()
                                          .getPackage());

        JavaFileInfoTranslator fileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();

        getBeanFiles(curNode).getJavaImportData().addImportInfo(
                caseImportInfo, getCapitalCase(fileInfo.getJavaName()),
                fileInfo.getPackage());
    }

    /**
     * Adds leaf attributes in generated files.
     *
     * @param entry    JAVA leaf info container
     * @param config   plugin config
     * @param curNode  current node
     * @param leafList flag indicating whether leaf container is leafList
     * @throws IOException IO operation fail
     */
    private void addLeafInfoToTempFiles(JavaLeafInfoContainer entry,
                                        YangPluginConfig config,
                                        YangNode curNode,
                                        boolean leafList)
            throws IOException {
        if (entry != null) {
            if (curNode instanceof RpcNotificationContainer) {
                TempJavaBeanFragmentFiles tempFiles = getBeanFiles(curNode);
                addJavaSnippetInfoToApplicableTempFiles(
                        getJavaAttributeOfLeaf(tempFiles, entry,
                                               config, leafList), config);
            } else {
                addJavaSnippetInfoToApplicableTempFiles(
                        getJavaAttributeOfLeaf(this, entry,
                                               config, leafList), config);
            }
        }
    }

    /**
     * Adds all the leaves in the current data model node as part of the
     * generated temporary file.
     *
     * @param curNode current node
     * @param config  plugin config
     * @throws IOException IO operation fail
     */
    protected void addCurNodeLeavesInfoToTempFiles(YangNode curNode,
                                                   YangPluginConfig config)
            throws IOException {
        if (!(curNode instanceof YangLeavesHolder)) {
            throw new TranslatorException(getErrorMsg(INVALID_LEAF_HOLDER,
                                                      curNode));
        }
        YangLeavesHolder leavesHolder = (YangLeavesHolder) curNode;

        for (YangLeaf leaf : leavesHolder.getListOfLeaf()) {
            addLeafInfoToTempFiles((JavaLeafInfoContainer) leaf, config,
                                   curNode, false);
        }

        for (YangLeafList leafList : leavesHolder.getListOfLeafList()) {
            addLeafInfoToTempFiles((JavaLeafInfoContainer) leafList, config,
                                   curNode, true);
        }
    }

    /**
     * Adds operation type to temp files.
     *
     * @param curNode current YANG node
     * @param config  YANG plugin config
     * @throws IOException IO exception
     */
    protected void addOperationTypeToTempFiles(YangNode curNode,
                                               YangPluginConfig config)
            throws IOException {
        JavaQualifiedTypeInfoTranslator typeInfo =
                new JavaQualifiedTypeInfoTranslator();
        typeInfo.setClassInfo(OPERATION_TYPE_CLASS);
        JavaFileInfo curInfo = ((JavaFileInfoContainer) curNode).getJavaFileInfo();
        JavaFileInfo info;
        if (curNode instanceof RpcNotificationContainer) {
            info = ((JavaFileInfoContainer) curNode).getJavaFileInfo();
        } else {
            info = ((JavaFileInfoContainer) getModuleNode(curNode))
                    .getJavaFileInfo();
        }

        typeInfo.setPkgInfo(info.getPackage().toLowerCase() + PERIOD +
                                    getCapitalCase(info.getJavaName()));
        String curNodeName = getCapitalCase(curInfo.getJavaName());
        if (!(curNode instanceof RpcNotificationContainer)) {
            javaImportData.addImportInfo(typeInfo, curNodeName, curInfo
                    .getPackage());
        }
        JavaAttributeInfo attributeInfo =
                getAttributeInfoForTheData(typeInfo, YANG + curNodeName +
                        OPERATION_TYPE_ATTRIBUTE, null, false, false);
        addJavaSnippetInfoToApplicableTempFiles(attributeInfo, config);
    }

    private YangNode getModuleNode(YangNode curNode) {
        YangNode tempNode = curNode.getParent();
        while (!(tempNode instanceof RpcNotificationContainer)) {
            tempNode = tempNode.getParent();
        }
        return tempNode;
    }

    /**
     * Adds value leaf flag to temp files.
     *
     * @param config YANG plugin config
     * @param node   YANG node
     * @throws IOException IO exception
     */
    protected void addValueLeafFlag(YangPluginConfig config, YangNode node)
            throws IOException {
        JavaFileInfo info = ((JavaFileInfoContainer) node).getJavaFileInfo();
        JavaQualifiedTypeInfoTranslator typeInfo =
                new JavaQualifiedTypeInfoTranslator();
        typeInfo.setClassInfo(BIT_SET);
        typeInfo.setPkgInfo(JAVA_UTIL_PKG);
        this.getJavaImportData().addImportInfo(typeInfo, info.getJavaName(),
                                               info.getPackage());
        JavaAttributeInfo attributeInfo =
                getAttributeInfoForTheData(typeInfo, VALUE_LEAF, null, false, false);
        addJavaSnippetInfoToApplicableTempFiles(attributeInfo, config);
    }

    /**
     * Adds value leaf flag to temp files.
     *
     * @param config YANG plugin config
     * @throws IOException IO exception
     */
    protected void addSelectLeafFlag(YangPluginConfig config)
            throws IOException {
        JavaQualifiedTypeInfoTranslator typeInfo =
                new JavaQualifiedTypeInfoTranslator();
        typeInfo.setClassInfo(BIT_SET);
        typeInfo.setPkgInfo(JAVA_UTIL_PKG);
        JavaAttributeInfo attributeInfo =
                getAttributeInfoForTheData(typeInfo, SELECT_LEAF, null, false, false);
        addJavaSnippetInfoToApplicableTempFiles(attributeInfo, config);
    }

    /**
     * Adds value leaf flag to temp files.
     *
     * @param config YANG plugin config
     * @throws IOException IO exception
     */
    protected void addYangAugmentedMap(YangPluginConfig config)
            throws IOException {
        JavaQualifiedTypeInfoTranslator typeInfo =
                new JavaQualifiedTypeInfoTranslator();
        typeInfo.setClassInfo(AUGMENT_MAP_TYPE);
        //Fix for add yangAugmentedInfo in equals/hashcode/and to string method.
        typeInfo.setPkgInfo(null);
        JavaAttributeInfo attributeInfo =
                getAttributeInfoForTheData(typeInfo, YANG_AUGMENTED_INFO_MAP,
                                           null, false, false);
        addJavaSnippetInfoToApplicableTempFiles(attributeInfo, config);
    }

    /**
     * Adds the new attribute info to the target generated temporary files.
     *
     * @param newAttrInfo  new attribute info
     * @param pluginConfig plugin configurations
     * @throws IOException IO operation fail
     */
    void addJavaSnippetInfoToApplicableTempFiles(JavaAttributeInfo newAttrInfo,
                                                 YangPluginConfig pluginConfig)
            throws IOException {
        isAttributePresent = true;
        String attrName = newAttrInfo.getAttributeName();
        boolean required = !attrName.equals(VALUE_LEAF) &&
                !attrName.equals(SELECT_LEAF) &&
                !attrName.equals(YANG_AUGMENTED_INFO_MAP);
        if (tempFlagSet(ATTRIBUTES_MASK)) {
            addAttribute(newAttrInfo);
        }
        if (tempFlagSet(GETTER_FOR_INTERFACE_MASK)) {
            addGetterForInterface(newAttrInfo);
        }
        if (tempFlagSet(SETTER_FOR_INTERFACE_MASK) && required) {
            addSetterForInterface(newAttrInfo);
        }
        if (tempFlagSet(SETTER_FOR_CLASS_MASK) && required) {
            addSetterImpl(newAttrInfo);
        }
        if (tempFlagSet(HASH_CODE_IMPL_MASK)) {
            addHashCodeMethod(newAttrInfo);
        }
        if (tempFlagSet(EQUALS_IMPL_MASK)) {
            addEqualsMethod(newAttrInfo);
        }
        if (tempFlagSet(TO_STRING_IMPL_MASK)) {
            addToStringMethod(newAttrInfo);
        }
        if (tempFlagSet(EDIT_CONTENT_MASK)) {
            //TODO: add implementation for edit content match.
        }
        boolean listAttr = newAttrInfo.isListAttr();
        if (tempFlagSet(ADD_TO_LIST_IMPL_MASK) && listAttr) {
            addAddToListImpl(newAttrInfo);
        }
        if (tempFlagSet(ADD_TO_LIST_INTERFACE_MASK) && listAttr) {
            addAddToListInterface(newAttrInfo);
        }
        YangType attrType = newAttrInfo.getAttributeType();
        if (tempFlagSet(FILTER_CONTENT_MATCH_FOR_NODES_MASK) &&
                attrType == null && !attrName
                .contains(OPERATION_TYPE_ATTRIBUTE) && required) {
            addSubtreeFilteringForChildNode(newAttrInfo);
        }
        if (tempFlagSet(FILTER_CONTENT_MATCH_FOR_LEAF_MASK) &&
                !listAttr && attrType != null) {
            addSubTreeFilteringForLeaf(newAttrInfo);
        }
        if (tempFlagSet(FILTER_CONTENT_MATCH_FOR_LEAF_LIST_MASK) &&
                listAttr && attrType != null) {
            addSubtreeFilteringForLeafList(newAttrInfo);
        }
        if (tempFlagSet(LEAF_IDENTIFIER_ENUM_ATTRIBUTES_MASK) &&
                !listAttr && attrType != null) {
            leafCount++;
            addLeafIdAttributes(newAttrInfo, leafCount);
        }
        if (!newAttrInfo.isIntConflict() && !newAttrInfo.isLongConflict() &&
                !newAttrInfo.isShortConflict()) {
            if (tempFlagSet(GETTER_FOR_CLASS_MASK)) {
                addGetterImpl(newAttrInfo);
            }
            if (tempFlagSet(FROM_STRING_IMPL_MASK)) {
                JavaQualifiedTypeInfoTranslator typeInfo =
                        getQualifiedInfoOfFromString(newAttrInfo,
                                                     pluginConfig.getConflictResolver());
            /*
             * Create a new java attribute info with qualified information of
             * wrapper classes.
             */
                JavaAttributeInfo fromStringAttributeInfo =
                        getAttributeInfoForTheData(
                                typeInfo, newAttrInfo.getAttributeName(),
                                attrType, getIsQualifiedAccessOrAddToImportList(
                                        typeInfo), false);
                addFromStringMethod(newAttrInfo, fromStringAttributeInfo);
            }
        }
    }

    /**
     * Returns JAVA class name for non implementation classes.
     *
     * @param suffix for the class name based on the file type
     * @return java class name
     */
    protected String getJavaClassName(String suffix) {
        return getCapitalCase(javaFileInfo.getJavaName()) + suffix;
    }

    /**
     * Returns class name for implementation classes.
     *
     * @param node YANG node
     * @return java class name
     */
    private String getImplClassName(YangNode node) {
        if (node instanceof RpcNotificationContainer) {
            return getGeneratedJavaClassName() + OP_PARAM;
        }
        return DEFAULT_CAPS + getGeneratedJavaClassName();
    }

    /**
     * Returns the directory path.
     *
     * @return directory path
     */
    private String getDirPath() {
        return javaFileInfo.getPackageFilePath();
    }

    /**
     * Constructs java code exit.
     *
     * @param fileType generated file type
     * @param curNode  current YANG node
     * @throws IOException when fails to generate java files
     */
    public void generateJavaFile(int fileType, YangNode curNode)
            throws IOException {
        List<String> imports =
                getBeanFiles(curNode).getJavaImportData().getImports();
        if (curNode instanceof YangAugmentableNode) {
            addImportsForAugmentableClass(imports, true, true, curNode);
        }
        createPackage(curNode);

        //Generate java code.
        if ((fileType & INTERFACE_MASK) != 0 ||
                (fileType & BUILDER_INTERFACE_MASK) != 0) {

            //Create interface file.
            interfaceJavaFileHandle =
                    getJavaFileHandle(getJavaClassName(INTERFACE_FILE_NAME_SUFFIX));
            interfaceJavaFileHandle =
                    generateInterfaceFile(interfaceJavaFileHandle, imports,
                                          curNode, isAttributePresent);

            //Create builder interface file.
            if ((fileType & BUILDER_INTERFACE_MASK) != 0) {
                builderInterfaceJavaFileHandle =
                        getJavaFileHandle(getJavaClassName(
                                BUILDER_INTERFACE_FILE_NAME_SUFFIX));
                builderInterfaceJavaFileHandle =
                        generateBuilderInterfaceFile(
                                builderInterfaceJavaFileHandle,
                                curNode, isAttributePresent);
                    /*
                     * Append builder interface file to interface file and
                     * close it.
                     */
                mergeJavaFiles(builderInterfaceJavaFileHandle,
                               interfaceJavaFileHandle);
            }

            insertDataIntoJavaFile(interfaceJavaFileHandle, CLOSE_CURLY_BRACKET);
            validateLineLength(interfaceJavaFileHandle);
            if (curNode instanceof YangAugmentableNode) {
                addImportsForAugmentableClass(imports, false, true, curNode);
            }
        }

        if (!curNode.isOpTypeReq() && curNode instanceof YangCase) {
            removeCaseParentImport(curNode, imports);
        }

        if ((fileType & BUILDER_CLASS_MASK) != 0 ||
                (fileType & DEFAULT_CLASS_MASK) != 0) {
            if (isAttributePresent) {
                addImportsToStringAndHasCodeMethods(imports, true);
                addArrayListImport(imports);
            }

            addBitsAndBase64Imports(curNode, imports);
            if (curNode instanceof YangAugmentableNode) {
                addImportsForAugmentableClass(imports, true, false, curNode);
                if (curNode.isOpTypeReq()) {
                    addInvocationExceptionImport(imports);
                }
            }
            sortImports(imports);

            //Create impl class file.
            implClassJavaFileHandle =
                    getJavaFileHandle(getImplClassName(curNode));
            implClassJavaFileHandle =
                    generateDefaultClassFile(implClassJavaFileHandle,
                                             curNode, isAttributePresent,
                                             imports);

            //Create builder class file.
            if ((fileType & BUILDER_CLASS_MASK) != 0) {
                builderClassJavaFileHandle =
                        getJavaFileHandle(getJavaClassName(
                                BUILDER_CLASS_FILE_NAME_SUFFIX));
                builderClassJavaFileHandle =
                        generateBuilderClassFile(builderClassJavaFileHandle,
                                                 curNode,
                                                 isAttributePresent);

                //Append impl class to builder class and close it.
                mergeJavaFiles(builderClassJavaFileHandle,
                               implClassJavaFileHandle);
            }
            insertDataIntoJavaFile(implClassJavaFileHandle, CLOSE_CURLY_BRACKET);
            validateLineLength(implClassJavaFileHandle);
        }
        //Close all the file handles.
        freeTemporaryResources(false);
    }

    //Removes case's parent import.
    private void removeCaseParentImport(YangNode node, List<String> imports) {
        YangNode parent = node.getParent();
        JavaFileInfo info = ((JavaFileInfoContainer) parent).getJavaFileInfo();
        String impt = getImportString(info.getPackage(),
                                      getCapitalCase(info.getJavaName()));
        imports.remove(impt);
    }

    //Adds import for array list.
    private void addArrayListImport(List<String> imports) {
        if (imports.contains(javaImportData.getImportForList())) {
            imports.add(ARRAY_LIST_IMPORT);
        }
    }

    //Adds import for bitset and base64 list.
    private void addBitsAndBase64Imports(YangNode curNode, List<String> imports) {
        if (curNode instanceof YangLeavesHolder) {
            YangLeavesHolder holder = (YangLeavesHolder) curNode;
            String impt = getJavaImportData()
                    .getImportForToBitSet();
            if (!holder.getListOfLeaf().isEmpty() &&
                    !imports.contains(impt)) {
                imports.add(impt);
            }
        }
    }

    /**
     * Adds imports for ToString and HashCodeMethod.
     *
     * @param imports   import list
     * @param operation add or remove
     */
    protected void addImportsToStringAndHasCodeMethods(List<String> imports,
                                                       boolean operation) {
        if (operation) {
            imports.add(javaImportData.getImportForHashAndEquals());
            imports.add(javaImportData.getImportForToString());
        } else {
            imports.remove(javaImportData.getImportForHashAndEquals());
            imports.remove(javaImportData.getImportForToString());
        }
        // TODO change boolean to OPERATION, in all related places.
    }

    /**
     * Adds import for map and hash map.
     *
     * @param imports         import list
     * @param operations      true for adding and false for deletion
     * @param isInterfaceFile if need to add in interface file
     * @param curNode         current node
     */
    private void addImportsForAugmentableClass(List<String> imports,
                                               boolean operations,
                                               boolean isInterfaceFile,
                                               YangNode curNode) {
        if (operations) {
            if (!isInterfaceFile) {
                imports.add(javaImportData.getHashMapImport());
            }
            // Add import for hash map only if node is not a YANG choice.
            if (!(curNode instanceof YangChoice)) {
                imports.add(javaImportData.getMapImport());
            }
        } else {
            if (!isInterfaceFile) {
                imports.remove(javaImportData.getHashMapImport());
            }
            imports.remove(javaImportData.getMapImport());
        }
        sortImports(imports);
    }

    /**
     * Adds invocation exception import.
     *
     * @param imports list of imports
     */
    private void addInvocationExceptionImport(List<String> imports) {
        imports.add(INVOCATION_TARGET_EXCEPTION_IMPORT);
    }

    /**
     * Removes all temporary file handles.
     *
     * @param errorOccurred flag indicating if error occurred
     * @throws IOException when failed to delete the temporary files
     */
    public void freeTemporaryResources(boolean errorOccurred)
            throws IOException {
        /*
         * Close all java file handles and when error occurs delete the files.
         */
        if (javaFlagSet(INTERFACE_MASK)) {
            closeFile(interfaceJavaFileHandle, errorOccurred);
        }
        if (javaFlagSet(BUILDER_CLASS_MASK)) {
            closeFile(builderClassJavaFileHandle);
        }
        if (javaFlagSet(BUILDER_INTERFACE_MASK)) {
            closeFile(builderInterfaceJavaFileHandle);
        }
        if (javaFlagSet(DEFAULT_CLASS_MASK)) {
            closeFile(implClassJavaFileHandle, errorOccurred);
        }
        /*
         * Close all temporary file handles and delete the files.
         */
        if (tempFlagSet(GETTER_FOR_CLASS_MASK)) {
            closeFile(getterImplTempFileHandle);
        }
        if (tempFlagSet(ATTRIBUTES_MASK)) {
            closeFile(attributesTempFileHandle);
        }
        if (tempFlagSet(HASH_CODE_IMPL_MASK)) {
            closeFile(hashCodeImplTempFileHandle);
        }
        if (tempFlagSet(TO_STRING_IMPL_MASK)) {
            closeFile(toStringImplTempFileHandle);
        }
        if (tempFlagSet(EQUALS_IMPL_MASK)) {
            closeFile(equalsImplTempFileHandle);
        }
        if (tempFlagSet(FROM_STRING_IMPL_MASK)) {
            closeFile(fromStringImplTempFileHandle);
        }
        if (tempFlagSet(ADD_TO_LIST_IMPL_MASK)) {
            closeFile(addToListImplTempFileHandle);
        }
        if (tempFlagSet(ADD_TO_LIST_INTERFACE_MASK)) {
            closeFile(addToListInterfaceTempFileHandle);
        }
        if (tempFlagSet(LEAF_IDENTIFIER_ENUM_ATTRIBUTES_MASK)) {
            closeFile(leafIdAttributeTempFileHandle);
        }
        if (tempFlagSet(FILTER_CONTENT_MATCH_FOR_LEAF_MASK)) {
            closeFile(subtreeFilteringForLeafTempFileHandle);
        }
        if (tempFlagSet(FILTER_CONTENT_MATCH_FOR_LEAF_LIST_MASK)) {
            closeFile(getSubtreeFilteringForListTempFileHandle);
        }
        if (tempFlagSet(FILTER_CONTENT_MATCH_FOR_NODES_MASK)) {
            closeFile(getSubtreeFilteringForChildNodeTempFileHandle);
        }
        if (tempFlagSet(EDIT_CONTENT_MASK)) {
            closeFile(editContentTempFileHandle);
        }
    }

    /**
     * Returns if the attribute needs to be accessed in a qualified manner or
     * not, if it needs to be imported, then the same needs to be done.
     *
     * @param importInfo import info
     * @return status of the qualified access to the attribute
     */
    protected boolean getIsQualifiedAccessOrAddToImportList(
            JavaQualifiedTypeInfo importInfo) {
        return javaImportData
                .addImportInfo((JavaQualifiedTypeInfoTranslator) importInfo,
                               getGeneratedJavaClassName(),
                               javaFileInfo.getPackage());
    }

    /**
     * Returns temp file handle for add to list interface.
     *
     * @return temp file handle for add to list interface
     */
    public File getAddToListInterfaceTempFileHandle() {
        return addToListInterfaceTempFileHandle;
    }

    /**
     * Returns temp file handle for add to list impl.
     *
     * @return temp file handle for add to list impl
     */
    public File getAddToListImplTempFileHandle() {
        return addToListImplTempFileHandle;
    }

    /**
     * Returns temp file handle for leaf identifier attributes.
     *
     * @return temp file handle for leaf identifier attributes
     */
    public File getLeafIdAttributeTempFileHandle() {
        return leafIdAttributeTempFileHandle;
    }

    /**
     * Sets true if root node.
     *
     * @param rootNode true if root node
     */
    void setRootNode(boolean rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * Returns temp file for is content match.
     *
     * @return temp file for is content match
     */
    public File getSubtreeFilteringForLeafTempFileHandle() {
        return subtreeFilteringForLeafTempFileHandle;
    }

    /**
     * Returns temp file for edit content file.
     *
     * @return temp file for edit content file
     */
    public File getEditContentTempFileHandle() {
        return editContentTempFileHandle;
    }

    /**
     * Returns temp file for is content match.
     *
     * @return temp file for is content match
     */
    public File getGetSubtreeFilteringForListTempFileHandle() {
        return getSubtreeFilteringForListTempFileHandle;
    }

    /**
     * Returns temp file for is content match.
     *
     * @return temp file for is content match
     */
    public File getGetSubtreeFilteringForChildNodeTempFileHandle() {
        return getSubtreeFilteringForChildNodeTempFileHandle;
    }

    /**
     * Checks if a given flag is set in generated java files.
     * Returns true if ANY flag is set in a bitwise-ORed argument, e.g.
     * <pre>
     *    javaFlagSet(FOO | BAR)
     * </pre>
     * returns true if either FOO flag or BAR flag is set.
     *
     * @param flag input flag mask value
     * @return true if set, else false
     */
    private boolean javaFlagSet(int flag) {
        return (getGeneratedJavaFiles() & flag) != 0;
    }

    /**
     * Checks if a given flag is set in temp files.
     *
     * @param flag input flag mask value
     * @return true if set, else false
     */
    private boolean tempFlagSet(int flag) {
        return (tempFilesFlagSet & flag) != 0;
    }

    /**
     * Sets true if binary type is there for leaf/leaf-list.
     *
     * @param binary true if binary type is there for leaf/leaf-list
     */
    public void setBinary(boolean binary) {
        isBinary = binary;
    }

    /**
     * Returns true if binary type is there for leaf/leaf-list.
     *
     * @return true if binary type is there for leaf/leaf-list
     */
    public boolean isBinary() {
        return isBinary;
    }

}
