package org.jzb.majie;

import com.github.ixtf.japp.core.J;
import com.github.ixtf.persistence.IEntity;
import com.google.common.reflect.ClassPath;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import lombok.SneakyThrows;
import org.jzb.majie.domain.Attachment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;

import static java.util.stream.Collectors.toSet;

/**
 * @author jzb 2019-10-26
 */
public class Util {

    private static Path rootPath() {
        final Named named = Names.named("rootPath");
        final Key<Path> key = Key.get(Path.class, named);
        return MajieModule.getInstance(key);
    }

    public static File file(Attachment attachment) {
        final String id = attachment.getId();
        final LocalDate ld = J.localDate(attachment.getCreateDateTime());
        final String year = String.valueOf(ld.getYear());
        final String month = String.valueOf(ld.getMonthValue());
        final String day = String.valueOf(ld.getDayOfMonth());
        return rootPath().resolve(Path.of("db", "upload", year, month, day, id)).toFile();
    }

    public static <T extends IEntity> Path luceneIndexPath(Class<T> entityClass) {
        final Path path = Path.of("db", "lucene", entityClass.getSimpleName());
        return rootPath().resolve(path);
    }

    public static <T extends IEntity> Path luceneTaxoPath(Class<T> entityClass) {
        final Path path = Path.of("db", "lucene", entityClass.getSimpleName() + "_Taxonomy");
        return rootPath().resolve(path);
    }

    public static <T> Collection<T> collectSubInstance(Class<T> clazz) {
        return collectSubInstance(clazz, clazz.getPackageName());
    }

    @SneakyThrows(IOException.class)
    public static <T> Collection<T> collectSubInstance(Class<T> clazz, String pkgName) {
        return ClassPath.from(Thread.currentThread().getContextClassLoader())
                .getTopLevelClasses(pkgName)
                .parallelStream()
                .map(ClassPath.ClassInfo::load)
                .filter(clazz::isAssignableFrom)
                .filter(it -> {
                    final int mod = it.getModifiers();
                    return !Modifier.isAbstract(mod) && !Modifier.isInterface(mod);
                })
                .map(MajieModule::getInstance)
                .map(clazz::cast)
                .collect(toSet());
    }
}
