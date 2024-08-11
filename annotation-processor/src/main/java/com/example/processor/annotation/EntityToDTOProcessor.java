package annotation;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@SupportedAnnotationTypes("com.example.annotation.EntityToDTO")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class EntityToDTOProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(EntityToDTO.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                continue;
            }

            TypeElement classElement = (TypeElement) element;
            EntityToDTO annotation = classElement.getAnnotation(EntityToDTO.class);
            String dtoName = annotation.dtoName();

            TypeSpec.Builder dtoBuilder = TypeSpec.classBuilder(dtoName)
                                                  .addModifiers(Modifier.PUBLIC);

            for (Element enclosed : classElement.getEnclosedElements()) {
                if (enclosed.getKind() == ElementKind.FIELD) {
                    VariableElement varElement = (VariableElement) enclosed;
                    FieldSpec
                            field = FieldSpec.builder(TypeName.get(varElement.asType()), varElement.getSimpleName().toString(), Modifier.PRIVATE)
                                             .build();
                    dtoBuilder.addField(field);
                }
            }

            TypeSpec dtoClass = dtoBuilder.build();
            JavaFile
                    javaFile = JavaFile.builder(processingEnv.getElementUtils().getPackageOf(classElement).getQualifiedName().toString(), dtoClass)
                                       .build();

            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error generating DTO: " + e.getMessage());
            }
        }
        return true;
    }
}
