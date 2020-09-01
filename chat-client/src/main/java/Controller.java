import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private NetWork netWork;

    @FXML
    TextField msgField;

    @FXML
    TextArea mainArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //при запуске клиента подключаемся к серверу. лучше сделать подключение из клиента, после нажатия кнопки в клиенте
        netWork = new NetWork((args -> {
            mainArea.appendText((String) args[0]);
        }));
    }

    public void sendMsgAction(ActionEvent actionEvent) {
        netWork.sendMessage(msgField.getText());
        msgField.clear();
        //перекидываем фокус, что бы пользователь снова мог писать сообщение в окне
        msgField.requestFocus();
    }
}
