package insyncwithfoo.ryecharm

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project


internal typealias ImportantNotificationGroup = NotificationGroup
internal typealias UnimportantNotificationGroup = NotificationGroup


private const val IMPORTANT_GROUP_ID = "${RyeCharm.ID}.important"
private const val UNIMPORTANT_GROUP_ID = "${RyeCharm.ID}.unimportant"
private val ICON = RyeIcons.TINY_16


private val notificationGroupManager: NotificationGroupManager
    get() = NotificationGroupManager.getInstance()


internal val importantNotificationGroup: ImportantNotificationGroup
    get() = notificationGroupManager.getNotificationGroup(IMPORTANT_GROUP_ID)


internal val unimportantNotificationGroup: UnimportantNotificationGroup
    get() = notificationGroupManager.getNotificationGroup(UNIMPORTANT_GROUP_ID)


private fun Notification.prettify() = this.apply {
    isImportant = false
    icon = ICON
}


internal fun Notification.runThenNotify(project: Project, action: Notification.() -> Unit) {
    run(action)
    notify(project)
}


internal fun NotificationGroup.error(title: String, content: String) =
    createNotification(title, content, NotificationType.ERROR).prettify()


internal fun NotificationGroup.warning(title: String, content: String) =
    createNotification(title, content, NotificationType.WARNING).prettify()


internal fun NotificationGroup.information(title: String, body: String) =
    createNotification(title, body, NotificationType.INFORMATION).prettify()
