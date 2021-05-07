package com.karmanchik.chtotibtelegrambot.bot.handler.helper;

import com.karmanchik.chtotibtelegrambot.bot.command.MainCommand;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.entity.ChatUser;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.entity.enums.WeekType;
import com.karmanchik.chtotibtelegrambot.jpa.JpaChatUserRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaGroupRepository;
import com.karmanchik.chtotibtelegrambot.model.Course;
import com.karmanchik.chtotibtelegrambot.model.IdGroupName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.ROLE_STUDENT;
import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.ROLE_TEACHER;

@Log4j2
@Component
@RequiredArgsConstructor
public class HandlerHelper {
    private final JpaGroupRepository groupRepository;
    private final JpaChatUserRepository userRepository;


    public static PartialBotApiMethod<? extends Serializable> selectRole(ChatUser chatUser) {
        return TelegramUtil.createMessageTemplate(chatUser)
                .text("Кто ты?")
                .replyMarkup(TelegramUtil.createReplyKeyboardMarkup()
                        .keyboardRow(TelegramUtil.createKeyboardRow(List.of(
                                ROLE_STUDENT,
                                ROLE_TEACHER)))
                        .oneTimeKeyboard(true)
                        .build())
                .build();
    }

    public static PartialBotApiMethod<? extends Serializable> inputMessage(ChatUser chatUser, String text) {
        return TelegramUtil.createMessageTemplate(chatUser)
                .text(text).build();
    }


    public static PartialBotApiMethod<? extends Serializable> mainMessage(ChatUser chatUser) {
        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup().build();
        LocalDate nextSchoolDate = DateHelper.getNextSchoolDate();
        String weekType = DateHelper.getWeekType().equals(WeekType.DOWN) ? "нижняя" : "верхняя";
        String name = nextSchoolDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru"));
        Role role = chatUser.getRole();


        markup.setKeyboard(List.of(
                TelegramUtil.createKeyboardRow(MainCommand.getKeyAll())));
        return role.equals(Role.STUDENT) ?
                TelegramUtil.createMessageTemplate(chatUser)
                        .text("1.\tРасписание на " + "<b>" + nextSchoolDate + "</b>" + " (" + name + ")\n" +
                                "2.\tРасписание на эту неделю (" + weekType + ")\n" +
                                "3.\tУзнать расписание педагога\n" +
                                "4.\tИзменить анкету")
                        .replyMarkup(markup).build() :
                TelegramUtil.createMessageTemplate(chatUser)
                        .text("1.\tРасписание на " + "<b>" + nextSchoolDate + "</b>" + " (" + name + ")\n" +
                                "2.\tРасписание на эту неделю (" + weekType + ")\n" +
                                "3.\tУзнать расписание группы\n" +
                                "4.\tИзменить анкету")
                        .replyMarkup(markup).build();
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
                            .text("Выбери группу...")
                            .replyMarkup(InlineKeyboardMarkup.builder()
                                    .keyboard(TelegramUtil.createInlineKeyboardButtons(groups, 3))
                                    .build())
                            .build());
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

}
