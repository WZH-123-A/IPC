import { marked } from 'marked'
import DOMPurify from 'dompurify'

marked.setOptions({ gfm: true, breaks: true })

/**
 * 将 Markdown 转为安全 HTML，用于 AI 回复等富文本展示
 * 支持：标题、加粗、表格、列表、代码块等
 */
export function renderMarkdown(markdown: string): string {
  if (!markdown || typeof markdown !== 'string') return ''
  const rawHtml = marked.parse(markdown)
  if (typeof rawHtml !== 'string') return markdown
  return DOMPurify.sanitize(rawHtml, {
    ALLOWED_TAGS: [
      'p', 'br', 'strong', 'em', 'b', 'i', 'u', 's', 'code', 'pre',
      'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
      'ul', 'ol', 'li', 'blockquote',
      'table', 'thead', 'tbody', 'tr', 'th', 'td',
      'a', 'span', 'div', 'hr',
    ],
    ALLOWED_ATTR: ['href', 'target', 'rel', 'class'],
  })
}
