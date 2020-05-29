/**
 * Интерфейс, определяющий требования к модели данных приложения.
 */

package ServiceClasses.ModelComponents;

import java.util.List;

public interface Model {
    int size();
    String getTextRow(int position);
    List<String> getTextRows(int position, int countRowsRead);
}
