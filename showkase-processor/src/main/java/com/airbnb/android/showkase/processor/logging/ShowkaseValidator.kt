package com.airbnb.android.showkase.processor.logging

import com.airbnb.android.showkase.annotation.ShowkaseComposable
import com.airbnb.android.showkase.annotation.ShowkaseRoot
import com.airbnb.android.showkase.annotation.ShowkaseRootModule
import com.airbnb.android.showkase.annotation.ShowkaseScreenshot
import com.airbnb.android.showkase.processor.exceptions.ShowkaseProcessorException
import com.airbnb.android.showkase.processor.models.ShowkaseMetadata
import com.airbnb.android.showkase.processor.models.kotlinMetadata
import kotlinx.metadata.Flag
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class ShowkaseValidator {
    @Suppress("ThrowsCount")
    internal fun validateComponentElement(
        element: Element,
        composableTypeMirror: TypeMirror,
        typeUtils: Types,
        annotationName: String,
        previewParameterTypeMirror: TypeMirror
    ) {
        val errorPrefix = "Error in ${element.simpleName}:"
        when {
            element.kind != ElementKind.METHOD -> {
                throw ShowkaseProcessorException(
                    "$errorPrefix Only composable methods can be annotated with $annotationName"
                )
            }
            element.annotationMirrors.find {
                typeUtils.isSameType(it.annotationType, composableTypeMirror)
            } == null -> {
                throw ShowkaseProcessorException(
                    "$errorPrefix Only composable methods can be annotated with $annotationName"
                )
            }
            element.modifiers.contains(Modifier.PRIVATE) -> {
                throw ShowkaseProcessorException(
                    "$errorPrefix The methods annotated with " +
                            "$annotationName can't be private as Showkase won't be able to access " +
                            "them otherwise."
                )
            }
            // Validate that only a single parameter is passed to these functions. In addition, 
            // the parameter should be annotated with @PreviewParameter.
            validateComposableParameter(
                element as ExecutableElement, previewParameterTypeMirror,
                typeUtils
            ) -> {
                throw ShowkaseProcessorException(
                    "$errorPrefix Make sure that the @Composable functions that you annotate with" +
                            " the $annotationName annotation only have a single parameter that is" +
                            " annotated with @PreviewParameter."
                )
            }
            else -> {
            }
        }
    }

    internal fun validateComposableParameter(
        element: ExecutableElement,
        previewParameterTypeMirror: TypeMirror,
        typeUtils: Types
    ): Boolean {
        
        val incorrectParameters = element.parameters
            .filter { paramElement ->
                val previewParameter = paramElement.annotationMirrors.find {
                    typeUtils.isSameType(it.annotationType, previewParameterTypeMirror)
                }
                paramElement.annotationMirrors.isNotEmpty() && previewParameter == null
            }
        
        // Return true if more than one parameter was passed to the @Composable function or if 
        // the parameter that was passed is not annotated with @PreviewParameter.
        return element.parameters.size > 1 || incorrectParameters.isNotEmpty()
    }

    internal fun validateColorElement(
        element: Element,
        annotationName: String
    ) {
        val errorPrefix = "Error in ${element.simpleName}:"
        when {
            element.kind != ElementKind.FIELD -> {
                throw ShowkaseProcessorException(
                    "$errorPrefix Only \"Color\" fields can be annotated with $annotationName"
                )
            }
            element.asType().kind != TypeKind.LONG -> {
                throw ShowkaseProcessorException(
                    "$errorPrefix Only \"Color\" fields can be annotated with $annotationName"
                )
            }
            // TODO(vinay.gaba) Also add the private modifier check. Unfortunately, the java code
            //  for this element adds a private modifier since it's a field. Potentially use 
            //  kotlinMetadata to enforce this check. 
            else -> {
            }
        }
    }

    internal fun validateTypographyElement(
        element: Element,
        annotationName: String,
        textStyleTypeMirror: TypeMirror,
        typeUtils: Types
    ) {
        val errorPrefix = "Error in ${element.simpleName}:"
        when {
            element.kind != ElementKind.FIELD -> {
                throw ShowkaseProcessorException(
                    "$errorPrefix Only \"TextStyle\" fields can be annotated with $annotationName"
                )
            }
            !typeUtils.isSameType(element.asType(), textStyleTypeMirror) -> {
                throw ShowkaseProcessorException(
                    "$errorPrefix Only \"TextStyle\" fields can be annotated with $annotationName"
                )
            }
            // TODO(vinay.gaba) Also add the private modifier check. Unfortunately, the java code
            //  for this element adds a private modifier since it's a field. Potentially use 
            //  kotlinMetadata to enforce this check. 
            else -> {
            }
        }
    }

    internal fun validateShowkaseRootElement(
        elementSet: Set<Element>,
        elementUtils: Elements,
        typeUtils: Types
    ) {
        if (elementSet.isEmpty()) return

        val showkaseRootAnnotationName = ShowkaseRoot::class.java.simpleName

        when {
            elementSet.size != 1 -> {
                throw ShowkaseProcessorException(
                    "Only one class in a module can be annotated with $showkaseRootAnnotationName"
                )
            }
            else -> {
                // Safe to do this as we've ensured that there's only one element in this set
                val element = elementSet.first()
                val errorPrefix = "Error in ${element.simpleName}:"

                requireClass(element, showkaseRootAnnotationName, errorPrefix)
                requireInterface(
                    element = element,
                    typeUtils = typeUtils,
                    interfaceTypeMirror = elementUtils
                        .getTypeElement(ShowkaseRootModule::class.qualifiedName)
                        .asType(),
                    annotationName = showkaseRootAnnotationName,
                    errorPrefix = errorPrefix
                )
            }
        }
    }

    private fun requireClass(
        element: Element,
        showkaseRootAnnotationName: String,
        errorPrefix: String
    ) {
        if (element.kind != ElementKind.CLASS) {
            throw ShowkaseProcessorException(
                "$errorPrefix Only classes can be annotated with @$showkaseRootAnnotationName"
            )
        }
    }

    @Suppress("LongParameterList")
    private fun requireInterface(
        element: Element,
        typeUtils: Types,
        interfaceTypeMirror: TypeMirror,
        annotationName: String,
        errorPrefix: String,
    ) {
        val interfaceName = typeUtils.asElement(interfaceTypeMirror).simpleName.toString()
        if (!typeUtils.isAssignable(element.asType(), interfaceTypeMirror)) {
            throw ShowkaseProcessorException(
                "$errorPrefix Only an implementation of ${interfaceName} can be annotated " +
                        "with @$annotationName"
            )
        }
    }

    fun validateEnclosingClass(
        enclosingClassTypeMirror: TypeMirror?,
        typeUtils: Types
    ) {
        val enclosingClassElement =
            enclosingClassTypeMirror?.let { typeUtils.asElement(it) } ?: return
        val kmClass =
            (enclosingClassElement.kotlinMetadata() as KotlinClassMetadata.Class).toKmClass()
        val errorPrefix = "Error in ${enclosingClassElement.simpleName}:"
        kmClass.constructors.forEach { constructor ->
            if (constructor.valueParameters.isNotEmpty()) {
                throw ShowkaseProcessorException(
                    "$errorPrefix Only classes that don't accept any constructor parameters can " +
                            "hold a @Composable function that's annotated with the " +
                            "@${ShowkaseComposable::class.java.simpleName}/@Preview annotation"
                )
            }
        }
    }

    internal fun validateShowkaseTestElement(
        elementSet: Set<Element>,
        elementUtils: Elements,
        typeUtils: Types
    ) {
        if (elementSet.isEmpty()) return

        val showkaseScreenshotAnnotationName = ShowkaseScreenshot::class.java.simpleName

        when {
            elementSet.size != 1 -> {
                throw ShowkaseProcessorException(
                    "Only a single class can be annotated with $showkaseScreenshotAnnotationName"
                )
            }
            else -> {
                // Safe to do this as we've ensured that there's only one element in this set
                val element = elementSet.first()
                val errorPrefix = "Error in ${element.simpleName}:"
                val showkaseScreenshotTestTypeMirror = elementUtils
                    .getTypeElement(SHOWKASE_SCREENSHOT_TEST_CLASS_NAME)
                    .asType()

                // Validate that the class annotated with @ShowkaseScreenshotTest is an abstract/open
                // class
                requireOpenClass(element, showkaseScreenshotAnnotationName, errorPrefix)

                // Validate that the class annotated with @ShowkaseScreenshot extends the
                // ShowkaseScreenshotTest interface
                requireInterface(
                    element = element,
                    typeUtils = typeUtils,
                    interfaceTypeMirror = showkaseScreenshotTestTypeMirror,
                    annotationName = showkaseScreenshotAnnotationName,
                    errorPrefix = errorPrefix
                )
            }
        }
    }

    private fun requireOpenClass(
        element: Element,
        annotationName: String,
        errorPrefix: String
    ) {
        val flags = (element.kotlinMetadata() as KotlinClassMetadata.Class).toKmClass().flags
        if (!Flag.IS_OPEN(flags) && !Flag.IS_ABSTRACT(flags)) {
            throw ShowkaseProcessorException(
                "$errorPrefix Class annotated with $annotationName needs to be an abstract/open class."
            )
        }
    }

    internal fun validateShowkaseComponents(
        componentsMetadata: Set<ShowkaseMetadata.Component>
    ) {
        val groupedComponents = componentsMetadata.groupBy { it.showkaseGroup }
        groupedComponents.forEach { groupEntry ->
            val groupedByNameComponents = groupEntry.value.groupBy { it.showkaseName }
            groupedByNameComponents.forEach { nameEntry ->
                // Verify that there's at most 1 default style for a given component
                if (nameEntry.value.filter { it.isDefaultStyle }.size > 1) {
                    throw ShowkaseProcessorException(
                        "Multiple styles for component: ${nameEntry.key} are current set as default style. " +
                                "Only one style is allowed to be the default style"
                    )
                }
            }
        }
    }

    companion object {
        private const val SHOWKASE_SCREENSHOT_TEST_CLASS_NAME =
            "com.airbnb.android.showkase.screenshot.testing.ShowkaseScreenshotTest"
    }
}
