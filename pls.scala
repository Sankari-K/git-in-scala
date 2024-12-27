package gitcommands

import java.awt.Desktop
import java.net.{URI, URL}


def plsWork(): Unit = {
    if (Desktop.isDesktopSupported) {
        Desktop.getDesktop.browse(new URI("https://git-scm.com/docs/user-manual"))
    }
}