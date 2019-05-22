;;_____________________________________________________________________________

;;      Init file for Emacs - Jean-Paul Le Fèvre
;;_____________________________________________________________________________

(blink-cursor-mode nil)
(set-language-environment "UTF-8")


(load-file "/home/lefevre/share/emacs.el")
(setq-default indent-tabs-mode nil)

(setq vc-handle-cvs nil)

;;_____________________________________________________________________________

(custom-set-variables
  ;; custom-set-variables was added by Custom -- don't edit or cut/paste it!
  ;; Your init file should contain only one such instance.
 '(Man-notify-method (quote bully))
 '(ange-ftp-ftp-program-name "nftp.pl")
 '(ange-ftp-gateway-ftp-program-name "sftp")
 '(default-input-method "latin-1-prefix")
 '(global-font-lock-mode t nil (font-lock))
 '(gnus-cache-active-file "/home/lefevre/etc/News/cache/active")
 '(gnus-directory "~/etc/News/")
 '(mail-source-directory "~/etc/Mail/")
 '(rmail-file-name "~/etc/RMAIL")
 '(show-paren-mode t nil (paren))
 '(smtpmail-debug-info nil)
 '(tramp-debug-buffer t)
 '(tramp-discard-garbage nil)
 '(tramp-remote-path (quote ("/bin" "/usr/bin" "/usr/local/bin")))
 '(transient-mark-mode t))
(custom-set-faces
  ;; custom-set-faces was added by Custom -- don't edit or cut/paste it!
  ;; Your init file should contain only one such instance.
 '(font-lock-comment-face ((t (:foreground "DarkSeaGreen"))))
 '(font-lock-constant-face ((((class color) (background light)) (:foreground "CadetBlue"))))
 '(font-lock-function-name-face ((((class color) (background light)) (:bold t :foreground "Blue"))))
 '(font-lock-keyword-face ((t (:foreground "RoyalBlue"))))
 '(font-lock-string-face ((t (:foreground "SeaGreen"))))
 '(font-lock-type-face ((t (:foreground "SlateBlue"))))
 '(font-lock-variable-name-face ((t (:foreground "navy")))))

(put 'downcase-region 'disabled nil)

(put 'narrow-to-region 'disabled nil)
