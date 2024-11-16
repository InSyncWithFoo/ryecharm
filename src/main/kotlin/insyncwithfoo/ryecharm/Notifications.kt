package insyncwithfoo.ryecharm

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project


internal typealias ErrorNotificationGroup = NotificationGroup
internal typealias InformationNotificationGroup = NotificationGroup


private const val ERROR_GROUP_ID = "${RyeCharm.ID}.errors"
private const val INFORMATION_GROUP_ID = "${RyeCharm.ID}.information"
private val ICON = RyeIcons.TINY_16


private val notificationGroupManager: NotificationGroupManager
    get() = NotificationGroupManager.getInstance()


internal val errorNotificationGroup: ErrorNotificationGroup
    get() = notificationGroupManager.getNotificationGroup(ERROR_GROUP_ID)


internal val informationNotificationGroup: InformationNotificationGroup
    get() = notificationGroupManager.getNotificationGroup(INFORMATION_GROUP_ID)


private fun Notification.prettify() = this.apply {
    isImportant = false
    icon = ICON
}


internal fun Notification.runThenNotify(project: Project, action: Notification.() -> Unit) {
    run(action)
    notify(project)
}


internal fun ErrorNotificationGroup.error(title: String, content: String) =
    createNotification(title, content, NotificationType.ERROR).prettify()


internal fun ErrorNotificationGroup.warning(title: String, content: String) =
    createNotification(title, content, NotificationType.WARNING).prettify()


internal fun InformationNotificationGroup.information(title: String, content: String) =
    createNotification(title, content, NotificationType.INFORMATION).prettify()
