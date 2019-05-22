;;_____________________________________________________________________________

;;	Init file for Emacs - Jean-Paul Le Fèvre
;;_____________________________________________________________________________

(setq desktop-enable t)
(global-font-lock-mode 1)
(setq jplf (getenv "HOME"))
(setq etcd (concat jplf "/etc/"))

(global-set-key "\C-x\C-a" 'iso-accents-mode)
(global-set-key "\C-x\C-j" 'join-line)

(setq text-mode-hook 'turn-on-auto-fill)
(put 'eval-expression 'disabled nil)
 
(defvar cursor-map-2 (make-keymap) "for ESC-[")
(fset 'Cursor-Map-2 cursor-map-2)
(define-key esc-map "[" 'Cursor-Map-2)

;;_____________________________________________________________________________

(setq load-path (append (list "/usr/share/emacs/site-lisp/elib") load-path))
(setq-default lpr-command "rlpr")

(global-set-key "\C-x\C-c" 'my-exit-from-emacs)
(global-set-key "\C-x\C-g" 'goto-line)
(global-set-key "\C-x\C-i" 'what-line)
(global-set-key "\C-x\C-y" 'fill-region)

(defun my-exit-from-emacs ()
  (interactive)
  (if (yes-or-no-p "Jean-Paul, veux-tu vraiment arreter emacs ? ")
      (save-buffers-kill-emacs)))

(setq-default case-fold-search nil)
(setq search-quote-char 92)
(setq search-reverse-char 46)
(setq inhibit-startup-message t)
(setq transient-mark-mode t)
(setq x-sensitive-text-pointer-shape 60)
(modify-frame-parameters (selected-frame) '((mouse-color . "blue")))


(load "paren")
(show-paren-mode t)

(setq ange-ftp-auto-save 1)

(setq vc-handle-cvs t)

;;_____________________________________________________________________________
;;
;; C and C++ mode constants
;;
(setq c-auto-newline nil)
(setq c-argdecl-indent 0)
(setq c-indent-level 4)
(setq c-tab-always-indent nil)
(setq c-brace-offset 0)
(setq c-continued-statement-offset 4)
(setq c-continued-brace-offset 4)
(setq c-label-offset 0)

(setq c++-electric-colon nil)
(setq c++-member-init-indent 4)
(setq c++-friend-offset 0)
(global-unset-key "\M-[")

(setq auto-mode-alist (cons '("\\.xsd$" . sgml-mode)
			    auto-mode-alist))
(setq auto-mode-alist (cons '("\\.jsp$" . html-mode)
			    auto-mode-alist))
(setq auto-mode-alist (cons '("\\.jspx$" . html-mode)
			    auto-mode-alist))
(setq auto-mode-alist (cons '("\\.ejs$" . html-mode)
			    auto-mode-alist))
(setq auto-mode-alist (cons '("\\.jade$" . html-mode)
			    auto-mode-alist))
(setq auto-mode-alist (cons '("\\.bsh$" . java-mode)
			    auto-mode-alist))
(setq auto-mode-alist (cons '("\\.policy$" . java-mode)
			    auto-mode-alist))
(setq auto-mode-alist (cons '("\\.scss$" . css-mode)
			    auto-mode-alist))
(setq auto-mode-alist (cons '("\\.less$" . css-mode)
			    auto-mode-alist))
(autoload 'python-mode "python-mode" "Python Mode." t)
(add-to-list 'auto-mode-alist '("\\.py\\'" . python-mode))
(add-to-list 'interpreter-mode-alist '("python" . python-mode))

;;_____________________________________________________________________________

(setq default-frame-alist
	'((width . 81) (height . 40)
	  (vertical-scroll-bars . nil)
	  (background-color . "AliceBlue")
	))

(autoload 'html-mode "sgml-mode" "HTML major mode." t)

;;_____________________________________________________________________________

(setq browse-url-browser-function 'browse-url-netscape)

(if (getenv "MAIL") (setq rmail-spool-directory
   (file-name-directory (getenv "MAIL"))))

(setq rmail-secondary-file-directory (concat etcd "mbox"))
(setq mail-archive-file-name (concat rmail-secondary-file-directory "/Sent"))
(setq mail-default-rmail-file (concat rmail-secondary-file-directory"/xmail"))
(setq rmail-default-file mail-default-rmail-file)
(setq rmail-secondary-file-regexp "[A-Z][A-z]*")
(setq rmail-delete-after-output t)
(setq rmail-mail-new-frame t)

(setq smtpmail-local-domain nil)
(setq smtpmail-debug-info t)
(setq send-mail-function 'smtpmail-send-it)
(setq mail-signature t)
(setq mail-aliases t)
(load-library "smtpmail")
;;
;; Christopher Davis provided this code to remove the signature before
;; Supercite processes the buffer.
;;
(defun ckd-delete-signature ()
  "Delete signature from reply buffer.
Shamelessly stolen from message-cite-original-without-signature."
  (let ((start (point))
	(end (mark t)))
    (goto-char end)
    (when (re-search-backward "^--[ \t]*$" start t)
      ;; Also peel off any blank lines before the signature.
      (forward-line -1)
      (while (looking-at "^[ \t]*$")
        (forward-line -1))
      (forward-line 1)
      (delete-region (point) end))
    (goto-char start)))

(add-hook 'mail-citation-hook 'sc-cite-original)
(add-hook 'sc-pre-hook 'ckd-delete-signature)

;;_____________________________________________________________________________
