package myself;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.List;

public class ZhanClickScreen extends Screen {
    private Module selectedModule = null;

    public ZhanClickScreen() {
        super(Component.literal("ZhanVisuals Menu"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // Затемнение фона
        g.fill(0, 0, width, height, 0x8806050C);

        int panelW = 460;
        int panelH = 280;
        int panelX = (width - panelW) / 2;
        int panelY = (height - panelH) / 2;

        // Основное окно меню
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xF0120F1A);
        g.renderOutline(panelX, panelY, panelW, panelH, 0xFFBD72FF);

        // Заголовок
        g.drawString(font, "§5ZhanVisuals §7| §dПанель управления", panelX + 15, panelY + 15, 0xFFFFFFFF, true);
        g.drawString(font, "ЛКМ - Вкл/Выкл | ПКМ - Настройки", panelX + 15, panelY + panelH - 20, 0xFFAAA1B8, true);

        // Левая панель со списком модулей
        int listX = panelX + 15;
        int listY = panelY + 40;
        int listW = 160;
        int listH = panelH - 65;
        g.fill(listX, listY, listX + listW, listY + listH, 0xFF0D0B12);

        // Пример рендеринга модулей в списке (здесь подставляется твой список модулей)
        // Список можно расширить по аналогии
        drawModuleItem(g, listX + 5, listY + 5, "TriggerBot / Aura", true, selectedModule != null && selectedModule.getName().contains("TriggerBot"));
        drawModuleItem(g, listX + 5, listY + 30, "ChestStealer", false, false);
        drawModuleItem(g, listX + 5, listY + 55, "AutoTotem", true, false);
        drawModuleItem(g, listX + 5, listY + 80, "NameTags", true, false);

        // Правая панель настроек выбранного модуля
        int setX = panelX + 190;
        int setY = panelY + 40;
        int setW = 255;
        int setH = panelH - 65;
        g.fill(setX, setY, setX + setW, setY + setH, 0xFF0D0B12);
        g.renderOutline(setX, setY, setW, setH, 0xFF352440);

        if (selectedModule != null) {
            g.drawString(font, "Настройки: §d" + selectedModule.getName(), setX + 12, setY + 12, 0xFFFFFFFF, true);
            // Здесь отображаются настройки модуля (например, дистанция, доп. дистанция и т.д.)
            g.drawString(font, "• Дистанция атаки: 3.2 §8[слайдер]", setX + 12, setY + 40, 0xFFE4B5FF, true);
            g.drawString(font, "• Доп. дистанция: 0.4 §8[слайдер]", setX + 12, setY + 65, 0xFFE4B5FF, true);
            g.drawString(font, "• Бить сквозь стены: [ВЫКЛ]", setX + 12, setY + 90, 0xFFFF7777, true);
            g.drawString(font, "• Учитывать кулдаун: [ВКЛ]", setX + 12, setY + 115, 0xFF55FF55, true);
        } else {
            g.drawCenteredString(font, "Выберите модуль слева", setX + setW / 2, setY + setH / 2 - 4, 0xFFAAA1B8);
        }

        super.render(g, mouseX, mouseY, partialTick);
    }

    private void drawModuleItem(GuiGraphics g, int x, int y, String name, boolean enabled, boolean selected) {
        int w = 150;
        int h = 22;
        int bgColor = selected ? 0xFF352440 : (enabled ? 0xFF1C1626 : 0xFF120F1A);
        g.fill(x, y, x + w, y + h, bgColor);
        g.drawString(font, name, x + 8, y + 7, enabled ? 0xFFF4EEFF : 0xFF7A708A, true);
        
        // Индикатор включения
        int indColor = enabled ? 0xFFBD72FF : 0xFF49305C;
        g.fill(x + w - 14, y + 7, x + w - 8, y + 15, indColor);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x();
        double my = event.y();

        int panelW = 460;
        int panelH = 280;
        int panelX = (width - panelW) / 2;
        int panelY = (height - panelH) / 2;

        int listX = panelX + 20;
        int listY = panelY + 45;

        // Клик по первому модулю (TriggerBot) для демонстрации выбора
        if (mx >= listX && mx <= listX + 150 && my >= listY && my <= listY + 22) {
            // Выбираем модуль для настройки справа
            // selectedModule = ...
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
