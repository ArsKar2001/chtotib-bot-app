package com.karmanchik.chtotibtelegrambot.bot.handler.helper;

import com.karmanchik.chtotibtelegrambot.bot.command.MainCommand;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.entity.*;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.entity.enums.WeekType;
import com.karmanchik.chtotibtelegrambot.model.GroupOrTeacher;
import com.karmanchik.chtotibtelegrambot.jpa.JpaGroupRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaTeacherRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaUserRepository;
import com.karmanchik.chtotibtelegrambot.model.Course;
import com.karmanchik.chtotibtelegrambot.model.IdGroupName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.ROLE_STUDENT;
import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.ROLE_TEACHER;

@Log4j2
@Component
@RequiredArgsConstructor
public class HandlerHelper {
    private final JpaGroupRepository groupRepository;
    private final JpaUserRepository userRepository;
    private final JpaTeacherRepository teacherRepository;


    public static PartialBotApiMethod<? extends Serializable> selectRole(ChatUser chatUser) {
        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow row = TelegramUtil.createKeyboardRow(List.of(
                ROLE_STUDENT,
                ROLE_TEACHER
        ));
        markup.setKeyboard(List.of(row));
        markup.setOneTimeKeyboard(true);

        return TelegramUtil.createMessageTemplate(chatUser)
                .setText("Кто ты?")
                .setReplyMarkup(markup);
    }

    public static PartialBotApiMethod<? extends Serializable> inputMessage(ChatUser chatUser, String text) {
        return TelegramUtil.createMessageTemplate(chatUser)
                .setText(text)
                .enableMarkdown(true);
    }


    public static PartialBotApiMethod<? extends Serializable> mainMessage(ChatUser chatUser) {
        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        LocalDate nextSchoolDate = DateHelper.getNextSchoolDate();
        String weekType = DateHelper.getWeekType().equals(WeekType.DOWN) ? "нижняя" : "верхняя";
        String name = nextSchoolDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru"));
        Role role = chatUser.getRole();


        markup.setKeyboard(List.of(
                TelegramUtil.createKeyboardRow(MainCommand.getKeyAll())));
        return role.equals(Role.STUDENT) ?
                TelegramUtil.createMessageTemplate(chatUser)
                        .setText("1.\tРасписание на " + "<b>" + nextSchoolDate + "</b>" + " (" + name + ")\n" +
                                "2.\tРасписание на эту неделю (" + weekType + ")\n" +
                                "3.\tУзнать расписание педагога\n" +
                                "4.\tИзменить анкету")
                        .setReplyMarkup(markup) :
                TelegramUtil.createMessageTemplate(chatUser)
                        .setText("1.\tРасписание на " + "<b>" + nextSchoolDate + "</b>" + " (" + name + ")\n" +
                                "2.\tРасписание на эту неделю (" + weekType + ")\n" +
                                "3.\tУзнать расписание группы\n" +
                                "4.\tИзменить анкету")
                        .setReplyMarkup(markup);
    }

    public List<PartialBotApiMethod<? extends Serializable>> selectGroup(ChatUser chatUser, String message) {
        if (Course.isCourse(message)) {
            Course course = Course.valueOf(message);
            Integer academicYear = TelegramUtil.getAcademicYear(course);
            int beginIndex = 2;
            String academicYearSuffix = academicYear.toString().substring(beginIndex);
            List<IdGroupName> groups = groupRepository.findAllByYearSuffix(academicYearSuffix);


            chatUser.setUserState(UserState.SELECT_GROUP);
            return List.of(
                    TelegramUtil.createMessageTemplate(userRepository.save(chatUser))
                            .setText("Выбери группу...")
                            .setReplyMarkup(new InlineKeyboardMarkup()
                                    .setKeyboard(TelegramUtil.createInlineKeyboardButtons(groups, 3))
                            ));
        }
        return Collections.emptyList();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum != null) try {
            Integer.parseInt(strNum);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
        else return false;
    }

    public static void setGroupOrTeachers(Role role, StringBuilder message, Lesson lesson) {
        if (role.equals(Role.STUDENT)) {
            lesson.getTeachers()
                    .forEach(t -> message.append(t.getName())
                            .append(" "));
        } else {
            message.append(lesson.getGroup().getName());
        }
        message.append("\n");
    }

    public GroupOrTeacher getData(ChatUser chatUser) {
        if (chatUser == null)
            return null;
        switch (chatUser.getRole()) {
            case STUDENT:
                return groupRepository.findByUsers(chatUser)
                        .orElseThrow();
            case TEACHER:
                return teacherRepository.findByUsers(chatUser)
                        .orElseThrow();
        }
        return null;
    }
}
