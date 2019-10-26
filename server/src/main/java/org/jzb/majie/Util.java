package org.jzb.majie;

import com.github.ixtf.japp.core.J;
import com.github.ixtf.persistence.IEntity;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.jzb.majie.domain.Attachment;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;

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
}
