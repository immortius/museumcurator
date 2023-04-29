package xyz.immortius.museumcurator.config.system;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.immortius.museumcurator.common.MuseumCuratorConstants;

import java.lang.reflect.Field;

/**
 * A boolean field
 */
public class BooleanFieldMetadata extends FieldMetadata<Boolean> {
    public static final Logger LOGGER = LogManager.getLogger(MuseumCuratorConstants.MOD_ID);

    private final Field field;

    public BooleanFieldMetadata(Field field, String name, String comment) {
        super(field, name, comment);
        Preconditions.checkArgument(Boolean.TYPE.equals(field.getType()));
        this.field = field;
        this.field.setAccessible(true);
    }

    @Override
    public String serializeValue(Object object) {
        try {
            return field.get(object).toString();
        } catch (IllegalAccessException e) {
            throw new ConfigException("Failed to retrieve " + getName() + " from object " + object, e);
        }
    }

    @Override
    public void deserializeValue(Object object, String value) {
        try {
            if ("true".equalsIgnoreCase(value)) {
                field.set(object, true);
            } else if ("false".equalsIgnoreCase(value)) {
                field.set(object, false);
            } else {
                LOGGER.warn("Invalid boolean value {} for configuration item {}", value, getName());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set " + getName() + " to value " + value, e);
        }
    }
}
