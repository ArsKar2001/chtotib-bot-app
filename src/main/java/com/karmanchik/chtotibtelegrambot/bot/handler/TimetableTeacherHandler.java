package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.DateHelper;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.Helper;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.entity.ChatUser;
import com.karmanchik.chtotibtelegrambot.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.entity.Teacher;
import com.karmanchik.chtotibtelegrambot.entity.enums.BotState;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.entity.enums.WeekType;
import com.karmanchik.chtotibtelegrambot.exception.ResourceNotFoundException;
import com.karmanchik.chtotibtelegrambot.jpa.JpaLessonsRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaTeacherRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaUserRepository;
import com.karmanchik.chtotibtelegrambot.model.IdTeacherName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;

import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.CANCEL;
import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.MESSAGE_SPLIT;

@Log4j2
@Component
@RequiredArgsConstructor
public class TimetableTeacherHandler implements Handler {
    private final JpaTeacherRepository teacherRepository;
    private final JpaUserRepository userRepository;
    private final JpaLessonsRepository lessonsRepository;

    public List<PartialBotApiMethod<? extends Serializable>> handle(ChatUser chatUser, String message) throws ResourceNotFoundException {
        switch (chatUser.getUserState()) {
            case SELECT_TEACHER:
                return selectTeacherOrAccept(chatUser, message);
            case INPUT_TEXT:
                return selectTeacher(chatUser, message);
            default:
                return List.of(
                        cancel(chatUser)
                );
        }
    }

    public static List<PartialBotApiMethod<? extends Serializable>> start(ChatUser chatUser) {
        return List.of(
                TelegramUtil.createMessageTemplate(chatUser)
                        .setText("Введите фамилию...")
                        .enableMarkdown(true)
        );
    }

    private List<PartialBotApiMethod<? extends Serializable>> selectTeacher(ChatUser chatUser, String message) {
        List<IdTeacherName> teacherNames = teacherRepository.findAllByName(message.toLowerCase());
        if (!teacherNames.isEmpty()) {
            chatUser.setUserState(UserState.SELECT_TEACHER);
            ChatUser save = userRepository.save(chatUser);

            final InlineKeyboardMarkup markup1 = new InlineKeyboardMarkup();
            markup1.setKeyboard(TelegramUtil.createInlineKeyboardButtons(teacherNames, 2));

            final ReplyKeyboardMarkup markup2 = TelegramUtil.createReplyKeyboardMarkup();
            final KeyboardRow row = TelegramUtil.createKeyboardRow(List.of(ConstantsHandler.CANCEL));
            markup2.setKeyboard(List.of(row))
                    .setOneTimeKeyboard(true);

            return sendMessageItIsYou(save, markup1, markup2);
        }
        return sendMessageNotFound(chatUser);
    }

    private List<PartialBotApiMethod<? extends Serializable>> selectTeacherOrAccept(ChatUser chatUser, String message) throws ResourceNotFoundException {
        if (message.equalsIgnoreCase(CANCEL)) {
            chatUser.setUserState(UserState.INPUT_TEXT);
            return start(userRepository.save(chatUser));
        } else if (HandlerHelper.isNumeric(message)) {
            int id = Integer.parseInt(message);
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(id, Teacher.class));
            return List.of(
                    createMessage(teacher, chatUser),
                    cancel(chatUser)
            );
        }
        return Collections.emptyList();
    }

    private PartialBotApiMethod<? extends Serializable> createMessage(Teacher teacher, ChatUser chatUser) {
        WeekType weekType = DateHelper.getWeekType();
        StringBuilder message = new StringBuilder();

        List<Lesson> lessons = lessonsRepository.findByTeacherOrderByPairNumberAsc(teacher);
        message.append("Расписание ").append("<b>").append(teacher.getName()).append("</b>:").append("\n");
        lessons.stream()
                .map(Lesson::getDay)
                .distinct()
                .sorted()
                .forEach(day -> {
                    String displayName = DayOfWeek.of(day).getDisplayName(TextStyle.FULL, Helper.getLocale());
                    message.append(MESSAGE_SPLIT).append("\n")
                            .append("<b>").append(displayName).append("</b>:").append("\n");
                    lessons.stream()
                            .filter(lesson -> lesson.getDay().equals(day))
                            .filter(lesson -> lesson.getWeekType() == weekType || lesson.getWeekType() == WeekType.NONE)
                            .forEach(Helper.getLessonTeacher(message));
                });
        return TelegramUtil.createMessageTemplate(chatUser)
                .setText(message.toString());
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendMessageItIsYou(ChatUser chatUser,
                                                                                 InlineKeyboardMarkup markup1,
                                                                                 ReplyKeyboardMarkup markup2) {
        return List.of(
                TelegramUtil.createMessageTemplate(chatUser)
                        .setText("Выберите педагога")
                        .setReplyMarkup(markup1),
                TelegramUtil.createMessageTemplate(chatUser)
                        .setText("...")
                        .setReplyMarkup(markup2));
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendMessageNotFound(ChatUser chatUser) {
        String outMessage = "Педагог не найден";
        return List.of(
                TelegramUtil.createMessageTemplate(chatUser)
                        .setText(outMessage)
                        .enableMarkdown(false),
                cancel(chatUser)
        );
    }

    private PartialBotApiMethod<? extends Serializable> cancel(ChatUser chatUser) {
        chatUser.setUserState(UserState.NONE);
        return HandlerHelper.mainMessage(userRepository.save(chatUser));
    }

    public BotState operatedBotState() {
        return BotState.AUTHORIZED;
    }

    public List<Role> operatedUserRoles() {
        return List.of(Role.STUDENT);
    }

    public List<UserState> operatedUserSate() {
        return List.of(
                UserState.INPUT_TEXT,
                UserState.SELECT_TEACHER
        );
    }
}
